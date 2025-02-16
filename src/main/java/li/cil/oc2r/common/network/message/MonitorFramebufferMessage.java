/* SPDX-License-Identifier: MIT */

package li.cil.oc2r.common.network.message;

import li.cil.oc2r.common.blockentity.MonitorBlockEntity;
import li.cil.oc2r.common.network.MessageUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.nio.ByteBuffer;

public final class MonitorFramebufferMessage extends AbstractMessage {
    private BlockPos pos;
    private ByteBuffer frame;

    ///////////////////////////////////////////////////////////////////

    public MonitorFramebufferMessage(final BlockPos projectorPos, final ByteBuffer frame) {
        this.pos = projectorPos;
        this.frame = frame;
    }

    public MonitorFramebufferMessage(final FriendlyByteBuf buffer) {
        super(buffer);
    }

    ///////////////////////////////////////////////////////////////////

    @Override
    public void fromBytes(final FriendlyByteBuf buffer) {
        pos = buffer.readBlockPos();
        frame = ByteBuffer.allocateDirect(buffer.readVarInt());
        buffer.readBytes(frame);
        frame.flip();
    }

    @Override
    public void toBytes(final FriendlyByteBuf buffer) {
        buffer.writeBlockPos(pos);
        buffer.writeVarInt(frame.limit());
        buffer.writeBytes(frame);
        frame.position(0);
    }

    ///////////////////////////////////////////////////////////////////

    @Override
    protected void handleMessage(final NetworkEvent.Context context) {
        MessageUtils.withClientBlockEntityAt(pos, MonitorBlockEntity.class,
            monitor -> monitor.applyNextFrameClient(frame));
    }
}
