package li.cil.oc2.common.blockentity;

import li.cil.oc2.api.capabilities.NetworkInterface;
import li.cil.oc2.common.Config;
import li.cil.oc2.common.Constants;
import li.cil.oc2.common.capabilities.Capabilities;
import li.cil.oc2.common.util.LazyOptionalUtils;
import li.cil.oc2.common.util.LevelUtils;
import li.cil.oc2.common.vxlan.TunnelManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.stream.Stream;

public final class VxlanBlockEntity extends ModBlockEntity implements NetworkInterface, TickableBlockEntity {
    private static final int TTL_COST = 1;
    //private int vti = ((int) (Math.random() * Integer.MAX_VALUE)) & 0x00ff_ffff;
    private int vti = 1000;
    private int frameCount;
    private long lastGameTime;

    private final Queue<byte[]> packetQueue = new ArrayBlockingQueue<>(32);

    ///////////////////////////////////////////////////////////////////

    // Each face and the default TunnelInterface connecting to the outernet
    private final NetworkInterface[] adjacentBlockInterfaces = new NetworkInterface[Constants.BLOCK_FACE_COUNT + 1];
    private boolean haveAdjacentBlocksChanged = true;

    ///////////////////////////////////////////////////////////////////

    public VxlanBlockEntity(final BlockPos pos, final BlockState state) {
        super(BlockEntities.VXLAN_HUB.get(), pos, state);
    }


    ///////////////////////////////////////////////////////////////////

    public void handleNeighborChanged() {
        haveAdjacentBlocksChanged = true;
    }

    @Override
    public byte[] readEthernetFrame() {
        return null;
    }

    @Override
    public void writeEthernetFrame(final NetworkInterface source, final byte[] frame, final int timeToLive) {
        if (level == null) {
            return;
        }

        final long gameTime = level.getGameTime();
        if (gameTime > lastGameTime) {
            lastGameTime = gameTime;
            frameCount = 1;
        } else if (frameCount > Config.hubEthernetFramesPerTick) {
            return;
        } else {
            frameCount++;
        }

        getAdjacentInterfaces().forEach(adjacentInterface -> {
            if (adjacentInterface != source) {
                adjacentInterface.writeEthernetFrame(this, frame, timeToLive - TTL_COST);
            }
        });
    }

    @Override
    public void serverTick() {
        if (level == null) {
            return;
        }

        if (adjacentBlockInterfaces[0] != null) {
            // CircularFifoQueue isn't thread-safe, so we have to synchronize on it.
            synchronized (packetQueue) {
                packetQueue.forEach(packet -> writeEthernetFrame(adjacentBlockInterfaces[0], packet, 255));
                packetQueue.clear();
            }
        } else {
            System.out.printf("VXLAN block is unregistered upstream: VTI=%d\n", vti);
        }
    }

    ///////////////////////////////////////////////////////////////////

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        if (level != null && !level.isClientSide() && tag.contains("vti")) {
            vti = tag.getInt("vti");
        }
    }

    @Override
    public void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        if (level != null && !level.isClientSide()) {
            tag.putInt("vti", vti);
        }
    }

    @Override
    protected void onUnload(final boolean isRemove) {
        if (level != null && !level.isClientSide()) {
            adjacentBlockInterfaces[0] = null;
            TunnelManager.instance().unregisterVti(vti);
        }

        super.onUnload(isRemove);
    }

    @Override
    public void loadServer() {
        adjacentBlockInterfaces[0] = TunnelManager.instance().registerVti(vti, this.packetQueue);
    }

    ///////////////////////////////////////////////////////////////////



    @Override
    protected void collectCapabilities(final CapabilityCollector collector, @Nullable final Direction direction) {
        collector.offer(Capabilities.networkInterface(), this);
    }

    ///////////////////////////////////////////////////////////////////

    private Stream<NetworkInterface> getAdjacentInterfaces() {
        validateAdjacentBlocks();
        return Arrays.stream(adjacentBlockInterfaces).filter(Objects::nonNull);
    }

    private void validateAdjacentBlocks() {
        if (isRemoved() || !haveAdjacentBlocksChanged) {
            return;
        }

        for (final Direction side : Constants.DIRECTIONS) {
            adjacentBlockInterfaces[side.get3DDataValue() + 1] = null;
        }

        haveAdjacentBlocksChanged = false;

        if (level == null || level.isClientSide()) {
            return;
        }

        final BlockPos pos = getBlockPos();
        for (final Direction side : Constants.DIRECTIONS) {
            final BlockEntity neighborBlockEntity = LevelUtils.getBlockEntityIfChunkExists(level, pos.relative(side));
            if (neighborBlockEntity != null) {
                final LazyOptional<NetworkInterface> optional = neighborBlockEntity.getCapability(Capabilities.networkInterface(), side.getOpposite());
                optional.ifPresent(adjacentInterface -> {
                    adjacentBlockInterfaces[side.get3DDataValue() + 1] = adjacentInterface;
                    LazyOptionalUtils.addWeakListener(optional, this, (hub, unused) -> hub.handleNeighborChanged());
                });
            }
        }
    }
}
