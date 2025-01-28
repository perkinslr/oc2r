/* SPDX-License-Identifier: MIT */

package li.cil.oc2.common.blockentity;
import li.cil.oc2.api.util.*;
import com.dannyandson.tinypipes.api.IChanneledRedstone;
import li.cil.oc2.api.bus.device.object.Callback;
import li.cil.oc2.api.bus.device.object.DocumentedDevice;
import li.cil.oc2.api.bus.device.object.NamedDevice;
import li.cil.oc2.api.bus.device.object.Parameter;
import li.cil.oc2.api.util.Side;
import li.cil.oc2.common.Constants;
import li.cil.oc2.common.util.HorizontalBlockUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.state.BlockState;
import li.cil.oc2.api.bus.device.rpc.*;
import javax.annotation.Nullable;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.util.*;

import static java.util.Collections.singletonList;

public final class RedstoneInterfaceBlockEntity extends ModBlockEntity implements NamedDevice, DocumentedDevice, IChanneledRedstone, RPCEventSource {
    private static final String OUTPUT_TAG_NAME = "output";

    private static final String GET_REDSTONE_INPUT = "getRedstoneInput";
    private static final String GET_REDSTONE_OUTPUT = "getRedstoneOutput";
    private static final String SET_REDSTONE_OUTPUT = "setRedstoneOutput";

    private static final String GET_REDSTONE_CHANNEL_INPUT = "getChannelInput";
    private static final String GET_REDSTONE_CHANNEL_OUTPUT = "getChannelOutput";
    private static final String SET_REDSTONE_CHANNEL_OUTPUT = "setChannelOutput";


    private static final String SIDE = "side";
    private static final String VALUE = "value";

    private HashMap<Direction, HashMap<Integer,Integer>> inputFrequencies = new HashMap<Direction, HashMap<Integer,Integer>>(6);
    private HashMap<Direction, HashMap<Integer,Integer>> outputFrequencies = new HashMap<Direction, HashMap<Integer,Integer>>(6);

    private final HashMap<IEventSink, UUID> subscribers = new HashMap();

    ///////////////////////////////////////////////////////////////////

    private final byte[] output = new byte[Constants.BLOCK_FACE_COUNT];

    ///////////////////////////////////////////////////////////////////

    public RedstoneInterfaceBlockEntity(final BlockPos pos, final BlockState state) {
        super(BlockEntities.REDSTONE_INTERFACE.get(), pos, state);
    }

    public int getRedstone(Direction d, int freq) {
        HashMap<Integer, Integer> sf = outputFrequencies.get(d);
        if (sf == null) {
            return 0;
        }
        return sf.getOrDefault(freq, 0);
    }
    public void setRedstone(Direction d, int freq, int value) {
        HashMap<Integer, Integer> sf = inputFrequencies.get(d);
        if (sf == null) {
            sf = new HashMap<Integer , Integer>(16);
            inputFrequencies.put(d, sf);
        }
        sf.put(freq, value);;

    }

    public int[] getChannels(Direction d) {
        HashMap<Integer, Integer> sf = outputFrequencies.get(d);
        if (sf == null) {
            return new int[]{};
        }
        return sf.keySet().stream().mapToInt(Integer::intValue).toArray();

    }

    ///////////////////////////////////////////////////////////////////

    @Override
    protected void saveAdditional(final CompoundTag tag) {
        super.saveAdditional(tag);

        tag.putByteArray(OUTPUT_TAG_NAME, output);
    }

    @Override
    public void load(final CompoundTag tag) {
        super.load(tag);
        final byte[] serializedOutput = tag.getByteArray(OUTPUT_TAG_NAME);
        System.arraycopy(serializedOutput, 0, output, 0, Math.min(serializedOutput.length, output.length));
    }

    public int getOutputForDirection(final Direction direction) {
        final Direction localDirection = HorizontalBlockUtils.toLocal(getBlockState(), direction);
        assert localDirection != null;

        return output[localDirection.get3DDataValue()];
    }

    @Callback(name = "listFrequencies")
    public String listFrequencies(Side side) {
        if (side == null) throw new IllegalArgumentException();

        if (level == null) {
            return "[[],[]]";
        }

        final BlockPos pos = getBlockPos();
        final Direction direction = HorizontalBlockUtils.toGlobal(getBlockState(), side);
        assert direction != null;


        ArrayList<String> segments = new ArrayList<String>();

        HashMap<Integer,Integer> hm = inputFrequencies.get(direction);
        ArrayList<String> subsegments = new ArrayList<String>();
        if (hm != null) {
            for (int f: hm.keySet()) {
                subsegments.add(""+f);
            }
        }
        segments.add("["+String.join(", ", subsegments)+"]");

        subsegments.clear();
        hm = outputFrequencies.get(direction);
        if (hm != null) {
            for (int f: hm.keySet()) {
                subsegments.add(""+f);
            }
        }
        segments.add("["+String.join(", ", subsegments)+"]");
        return "["+segments.get(0)+", "+segments.get(1)+"]";
    }

    @Callback(name = GET_REDSTONE_CHANNEL_INPUT)
    public int getChannelInput(final Side side, int channel) {
        if (side == null) throw new IllegalArgumentException();

        if (level == null) {
            return 0;
        }

        final BlockPos pos = getBlockPos();
        final Direction direction = HorizontalBlockUtils.toGlobal(getBlockState(), side);
        assert direction != null;
        HashMap<Integer,Integer> hm = inputFrequencies.get(direction);
        if (hm == null) {
            return 0;
        }
        return hm.getOrDefault(channel, 0);
    }

    @Callback(name = GET_REDSTONE_CHANNEL_OUTPUT)
    public int getChannelOutput(final Side side, int channel) {
            if (side == null) throw new IllegalArgumentException();

            if (level == null) {
                return 0;
            }

        final BlockPos pos = getBlockPos();
        final Direction direction = HorizontalBlockUtils.toGlobal(getBlockState(), side);
        assert direction != null;
        HashMap<Integer,Integer> hm = outputFrequencies.get(direction);
        if (hm == null) {
            return 0;
        }
        return hm.getOrDefault(channel, 0);
    }

    @Callback(name = SET_REDSTONE_CHANNEL_OUTPUT)
    public void setChannelOutput(final Side side, int channel, int value) {
        if (side == null) throw new IllegalArgumentException();

        if (level == null) {
            return;
        }

        final BlockPos pos = getBlockPos();
        final Direction direction = HorizontalBlockUtils.toGlobal(getBlockState(), side);
        assert direction != null;
        HashMap<Integer,Integer> hm = outputFrequencies.get(direction);
        if (hm == null) {
            hm = new HashMap<Integer,Integer>(16);
            outputFrequencies.put(direction, hm);
        }
        hm.put(channel, value);
        if (direction != null) {
            notifyNeighbor(direction);
        }

        setChanged();

    }


    @Callback(name = GET_REDSTONE_INPUT)
    public int getRedstoneInput(@Parameter(SIDE) @Nullable final Side side) {
        if (side == null) throw new IllegalArgumentException();

        if (level == null) {
            return 0;
        }

        final BlockPos pos = getBlockPos();
        final Direction direction = HorizontalBlockUtils.toGlobal(getBlockState(), side);
        assert direction != null;

        final BlockPos neighborPos = pos.relative(direction);
        final ChunkPos chunkPos = new ChunkPos(neighborPos);
        if (!level.hasChunk(chunkPos.x, chunkPos.z)) {
            return 0;
        }

        return level.getSignal(neighborPos, direction);
    }

    @Callback(name = GET_REDSTONE_OUTPUT, synchronize = false)
    public int getRedstoneOutput(@Parameter(SIDE) @Nullable final Side side) {
        if (side == null) throw new IllegalArgumentException();
        final int index = side.getDirection().get3DDataValue();

        return output[index];
    }

    @Callback(name = SET_REDSTONE_OUTPUT)
    public void setRedstoneOutput(@Parameter(SIDE) @Nullable final Side side, @Parameter(VALUE) final int value) {
        if (side == null) throw new IllegalArgumentException();
        final int index = side.getDirection().get3DDataValue();

        final byte clampedValue = (byte) Mth.clamp(value, 0, 15);
        if (clampedValue == output[index]) {
            return;
        }

        output[index] = clampedValue;

        final Direction direction = HorizontalBlockUtils.toGlobal(getBlockState(), side);
        if (direction != null) {
            notifyNeighbor(direction);
        }

        setChanged();
    }

    @Override
    public Collection<String> getDeviceTypeNames() {
        return singletonList("redstone");
    }

    @Override
    public void getDeviceDocumentation(final DeviceVisitor visitor) {
        visitor.visitCallback(GET_REDSTONE_CHANNEL_INPUT)
            .description("Get the current redstone level received on the specified channel and side. " +
                "Note that if the current output level on the specified side is not " +
                "zero, this will affect the measured level.\n" +
                "Sides may be specified by name or zero-based index. Please note that " +
                "the side depends on the orientation of the device.")
            .returnValueDescription("the current received level on the specified side.")
            .parameterDescription(SIDE, "the side to read the input level from.")
            .parameterDescription("channel", "the channel to read the input level from.");


        visitor.visitCallback(GET_REDSTONE_CHANNEL_OUTPUT)
            .description("Get the current redstone level transmitted on the specified channel side. " +
                "This will return the value last set via setRedstoneOutput().\n" +
                "Sides may be specified by name or zero-based index. Please note that " +
                "the side depends on the orientation of the device.")
            .returnValueDescription("the current transmitted level on the specified side.")
            .parameterDescription(SIDE, "the side to read the output level from.")
            .parameterDescription("channel", "the channel to read the output level from.");
        visitor.visitCallback(SET_REDSTONE_OUTPUT)
            .description("Set the new redstone level transmitted on the specified side.\n" +
                "Sides may be specified by name or zero-based index. Please note that " +
                "the side depends on the orientation of the device.")
            .parameterDescription(SIDE, "the side to write the output level to.")
            .parameterDescription("channel", "the channel to write the output level to.")
            .parameterDescription(VALUE, "the output level to set, will be clamped to [0, 15].");

        visitor.visitCallback(GET_REDSTONE_INPUT)
            .description("Get the current redstone level received on the specified side. " +
                "Note that if the current output level on the specified side is not " +
                "zero, this will affect the measured level.\n" +
                "Sides may be specified by name or zero-based index. Please note that " +
                "the side depends on the orientation of the device.")
            .returnValueDescription("the current received level on the specified side.")
            .parameterDescription(SIDE, "the side to read the input level from.");

        visitor.visitCallback(GET_REDSTONE_OUTPUT)
            .description("Get the current redstone level transmitted on the specified side. " +
                "This will return the value last set via setRedstoneOutput().\n" +
                "Sides may be specified by name or zero-based index. Please note that " +
                "the side depends on the orientation of the device.")
            .returnValueDescription("the current transmitted level on the specified side.")
            .parameterDescription(SIDE, "the side to read the output level from.");
        visitor.visitCallback(SET_REDSTONE_OUTPUT)
            .description("Set the new redstone level transmitted on the specified side.\n" +
                "Sides may be specified by name or zero-based index. Please note that " +
                "the side depends on the orientation of the device.")
            .parameterDescription(SIDE, "the side to write the output level to.")
            .parameterDescription(VALUE, "the output level to set, will be clamped to [0, 15].");
    }

    ///////////////////////////////////////////////////////////////////

    private void notifyNeighbor(final Direction direction) {
        if (level == null) {
            return;
        }

        level.updateNeighborsAt(getBlockPos(), getBlockState().getBlock());
        level.updateNeighborsAt(getBlockPos().relative(direction), getBlockState().getBlock());
    }

    @Override
    public void subscribe(IEventSink sink, UUID myid) {
        subscribers.put(sink, myid);
    }
    @Override
    public void unsubscribe(IEventSink sink) {
        subscribers.remove(sink);
    }

    public void neighborChanged(BlockPos fromPos) {
        int sl = 0;
        if (level == null) {
            return;
        }

        final BlockPos pos = getBlockPos();
        final Direction direction = Side.relativeDirection(pos, fromPos);
        assert direction != null;

        final ChunkPos chunkPos = new ChunkPos(fromPos);
        if (!level.hasChunk(chunkPos.x, chunkPos.z)) {
            sl = 0;
        }

        sl = level.getSignal(fromPos, direction);
        JsonObject msg = new JsonObject();
        msg.addProperty("event", "redstone");
        msg.addProperty("side", ""+direction);
        msg.addProperty("level", sl);

        for (var subscriber : subscribers.entrySet()) {
            subscriber.getKey().postEvent(subscriber.getValue(), msg);
        }
    }
}
