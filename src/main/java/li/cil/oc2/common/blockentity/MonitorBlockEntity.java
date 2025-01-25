/* SPDX-License-Identifier: MIT */

package li.cil.oc2.common.blockentity;

import li.cil.oc2.client.renderer.MonitorGUIRenderer;
import li.cil.oc2.common.Config;
import li.cil.oc2.common.block.MonitorBlock;
import li.cil.oc2.common.bus.device.DeviceGroup;
import li.cil.oc2.common.bus.device.vm.block.KeyboardDevice;
import li.cil.oc2.common.bus.device.vm.block.MonitorDevice;
import li.cil.oc2.common.capabilities.Capabilities;
import li.cil.oc2.common.container.MonitorDisplayContainer;
import li.cil.oc2.common.energy.FixedEnergyStorage;
import li.cil.oc2.common.network.MonitorLoadBalancer;
import li.cil.oc2.common.network.Network;
import li.cil.oc2.common.network.message.*;
import li.cil.oc2.common.vm.device.SimpleFramebufferDevice;
import li.cil.oc2.jcodec.codecs.h264.H264Decoder;
import li.cil.oc2.jcodec.codecs.h264.H264Encoder;
import li.cil.oc2.jcodec.codecs.h264.encode.CQPRateControl;
import li.cil.oc2.jcodec.common.model.ColorSpace;
import li.cil.oc2.jcodec.common.model.Picture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

import static li.cil.oc2.common.bus.device.vm.block.MonitorDevice.HEIGHT;
import static li.cil.oc2.common.bus.device.vm.block.MonitorDevice.WIDTH;

public final class MonitorBlockEntity extends ModBlockEntity implements TickableBlockEntity {
    @FunctionalInterface
    public interface FrameConsumer {
        void processFrame(final Picture picture);
    }

    ///////////////////////////////////////////////////////////////////

    private static final String STATE_TAG_NAME = "state";
    private static final String ENERGY_TAG_NAME = "energy";
    private static final String IS_RENDERING_TAG_NAME = "projecting";
    private static final String HAS_ENERGY_TAG_NAME = "has_energy";

    private static final ExecutorService DECODER_WORKERS = Executors.newCachedThreadPool(r -> {
        final Thread thread = new Thread(r);
        thread.setDaemon(true);
        thread.setName("Monitor Frame Decoder");
        return thread;
    });

    ///////////////////////////////////////////////////////////////////

    private final FixedEnergyStorage energy = new FixedEnergyStorage(Config.monitorEnergyStorage);

    ///////////////////////////////////////////////////////////////////

    private boolean hasEnergy;
    private boolean isMounted;
    private boolean isPowered;

    @Nullable private CompletableFuture<?> runningDecode;
    private final H264Decoder decoder = new H264Decoder();
    private final ByteBuffer decoderBuffer = ByteBuffer.allocateDirect(WIDTH * HEIGHT * SimpleFramebufferDevice.STRIDE);
    @Nullable private FrameConsumer frameConsumer;

    private boolean needsIDR;
    private final DeviceGroup deviceGroup = new DeviceGroup(this);
    private final MonitorDevice monitorDevice = new MonitorDevice(this, this::handleMountedChanged);
    private final KeyboardDevice<BlockEntity> keyboardDevice = new KeyboardDevice<>(this);
    private final Picture picture = Picture.create(WIDTH, HEIGHT, ColorSpace.YUV420J);
    private final MonitorGUIRenderer monitor = new MonitorGUIRenderer();

    private final H264Encoder encoder = new H264Encoder(new CQPRateControl(12));
    private final ByteBuffer encoderBuffer = ByteBuffer.allocateDirect(WIDTH * HEIGHT * SimpleFramebufferDevice.STRIDE);

    ///////////////////////////////////////////////////////////////////

    public void setRequiresKeyframe() {
        needsIDR = true;
    }

    public boolean hasPower() {
        return hasEnergy;
    }

    public boolean getPowerState() { return isPowered; }

    public boolean isMounted() { return isMounted; }

    public MonitorGUIRenderer getMonitor() { return monitor; }

    private long lastKeepAliveSentAt;

    public void handleInput(final int keycode, final boolean isDown) {
        keyboardDevice.sendKeyEvent(keycode, isDown);
    }

    @Nullable
    private ByteBuffer encodeFrame() {
        final boolean hasChanges = monitorDevice.applyChanges(picture);
        if (!hasChanges && !needsIDR) {
            return null;
        }

        encoderBuffer.clear();
        final ByteBuffer frameData;
        try {
            if (needsIDR) {
                frameData = encoder.encodeIDRFrame(picture, encoderBuffer);
                needsIDR = false;
            } else {
                frameData = encoder.encodeFrame(picture, encoderBuffer).data();
            }
        } catch (final BufferOverflowException ignored) {
            return null;
        }

        final Deflater deflater = new Deflater(Deflater.BEST_COMPRESSION);
        deflater.setInput(frameData);
        deflater.finish();
        final ByteBuffer compressedFrameData = ByteBuffer.allocateDirect(1024 * 1024);
        deflater.deflate(compressedFrameData, Deflater.FULL_FLUSH);
        deflater.end();
        compressedFrameData.flip();

        return compressedFrameData;
    }

    private void handleMountedChanged(final boolean value) {
        updateMonitorState(value, hasEnergy);
    }

    public void setFrameConsumer(@Nullable final FrameConsumer consumer) {
        if (consumer == frameConsumer) {
            return;
        }
        synchronized (picture) {
            this.frameConsumer = consumer;
            if (frameConsumer != null) {
                frameConsumer.processFrame(picture);
            }
        }
    }

    private void updateMonitorState(final boolean isMounted, final boolean hasEnergy) {
        if ((isMounted == this.isMounted && hasEnergy == this.hasEnergy) || !isValid()) {
            return;
        }

        // We may get called from unmount() of our device, which can be triggered due to chunk unload.
        // Hence, we need to check the loaded state here, lest we ghost load the chunk, breaking everything.
        if (level != null && !level.isClientSide() && level.isLoaded(getBlockPos())) {
            if (this.isMounted && !isMounted) {
                Arrays.fill(picture.getPlaneData(0), (byte) -128);
            }

            this.isMounted = isMounted;
            this.hasEnergy = hasEnergy;

            level.setBlock(getBlockPos(), getBlockState().setValue(MonitorBlock.LIT, isMounted), Block.UPDATE_CLIENTS);

            Network.sendToClientsTrackingBlockEntity(new MonitorStateMessage(this, isMounted, hasEnergy), this);
        }
    }

    public void applyMonitorStateClient(final boolean isRendering, final boolean hasEnergy) {
        if (level == null || !level.isClientSide()) {
            return;
        }

        this.isMounted = isRendering;
        this.hasEnergy = hasEnergy;
    }

    public MonitorBlockEntity(final BlockPos pos, final BlockState state) {
        super(BlockEntities.MONITOR.get(), pos, state);

        deviceGroup.addDevice(monitorDevice);
        deviceGroup.addDevice(keyboardDevice);

        encoder.setKeyInterval(100);

        // We want to unload devices even on level unload to free global resources.
        setNeedsLevelUnloadEvent();
    }

    public void start() {
        isPowered = true;
    }

    public void stop() {
        isPowered = false;
    }

    public void openTerminalScreen(final ServerPlayer player) {
        MonitorDisplayContainer.createServer(this, energy, player);
    }

    public void onRendering() {
        final long now = System.currentTimeMillis();
        if (now - lastKeepAliveSentAt > 1000) {
            lastKeepAliveSentAt = now;
            Network.sendToServer(new MonitorRequestFramebufferMessage(this));
        }
    }

    @Override
    public void serverTick() {
        if (level == null || !isValid()) {
            return;
        }

        final boolean hasPowered;
        if (Config.monitorsUseEnergy()) {
            hasPowered = energy.extractEnergy(Config.monitorEnergyPerTick, true) >= Config.monitorEnergyPerTick;
            if (hasPowered) {
                energy.extractEnergy(Config.monitorEnergyPerTick, false);
            }
        } else {
            hasPowered = true;
        }

        updateMonitorState(isMounted, hasPowered);

        if (!hasEnergy || !isPowered || (!monitorDevice.hasChanges() && !needsIDR)) {
            return;
        }

        MonitorLoadBalancer.offerFrame(this, this::encodeFrame);
    }

    public void applyNextFrameClient(final ByteBuffer frameData) {
        if (level == null || !level.isClientSide()) {
            return;
        }

        final CompletableFuture<?> lastDecode = runningDecode;
        runningDecode = CompletableFuture.runAsync(() -> {
            try {
                try {
                    if (lastDecode != null) lastDecode.join();
                } catch (final CompletionException ignored) {
                }

                final Inflater inflater = new Inflater();
                inflater.setInput(frameData);

                decoderBuffer.clear();
                inflater.inflate(decoderBuffer);
                decoderBuffer.flip();

                decoder.decodeFrame(decoderBuffer, picture.getData());

                synchronized (picture) {
                    if (frameConsumer != null) {
                        frameConsumer.processFrame(picture);
                    }
                }
            } catch (final DataFormatException ignored) {
            }
        }, DECODER_WORKERS);
    }

    @Override
    public CompoundTag getUpdateTag() {
        final CompoundTag tag = super.getUpdateTag();

        tag.putBoolean(IS_RENDERING_TAG_NAME, isMounted);
        tag.putBoolean(HAS_ENERGY_TAG_NAME, hasEnergy);
        tag.putBoolean(STATE_TAG_NAME, isPowered);
        return tag;
    }

    @Override
    public void handleUpdateTag(final CompoundTag tag) {
        super.handleUpdateTag(tag);

        isMounted = tag.getBoolean(IS_RENDERING_TAG_NAME);
        hasEnergy = tag.getBoolean(HAS_ENERGY_TAG_NAME);
        isPowered = tag.getBoolean(STATE_TAG_NAME);
    }

    @Override
    protected void saveAdditional(final CompoundTag tag) {
        super.saveAdditional(tag);

        tag.put(ENERGY_TAG_NAME, energy.serializeNBT());
        tag.putBoolean(IS_RENDERING_TAG_NAME, isPowered);
    }

    @Override
    public void load(final CompoundTag tag) {
        super.load(tag);

        energy.deserializeNBT(tag.getCompound(ENERGY_TAG_NAME));
        hasEnergy = tag.getBoolean(HAS_ENERGY_TAG_NAME);
        isPowered = tag.getBoolean(IS_RENDERING_TAG_NAME);
    }

    ///////////////////////////////////////////////////////////////////

    @Override
    protected void collectCapabilities(final CapabilityCollector collector, @Nullable final Direction direction) {
        if(direction != getBlockState().getValue(MonitorBlock.FACING)) {
            collector.offer(Capabilities.device(), deviceGroup);

            if (Config.monitorsUseEnergy()) {
                collector.offer(Capabilities.energyStorage(), energy);
            }
        }
    }
}
