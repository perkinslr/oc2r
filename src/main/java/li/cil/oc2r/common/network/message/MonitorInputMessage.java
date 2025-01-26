/* SPDX-License-Identifier: MIT */

package li.cil.oc2.common.network.message;

import li.cil.oc2.common.blockentity.MonitorBlockEntity;
import li.cil.oc2.common.network.MessageUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

public final class MonitorInputMessage extends AbstractMessage {
    private BlockPos pos;
    private int keycode;
    private boolean isDown;

    ///////////////////////////////////////////////////////////////////

    public MonitorInputMessage(final MonitorBlockEntity keyboard, final int keycode, final boolean isDown) {
        this.pos = keyboard.getBlockPos();
        this.keycode = keycode;
        this.isDown = isDown;
    }

    public MonitorInputMessage(final FriendlyByteBuf buffer) {
        super(buffer);
    }

    ///////////////////////////////////////////////////////////////////

    @Override
    public void fromBytes(final FriendlyByteBuf buffer) {
        pos = buffer.readBlockPos();
        keycode = buffer.readVarInt();
        isDown = buffer.readBoolean();
    }

    @Override
    public void toBytes(final FriendlyByteBuf buffer) {
        buffer.writeBlockPos(pos);
        buffer.writeVarInt(keycode);
        buffer.writeBoolean(isDown);
    }

    ///////////////////////////////////////////////////////////////////

    @Override
    protected void handleMessage(final NetworkEvent.Context context) {
        MessageUtils.withNearbyServerBlockEntityForInteraction(context, pos, MonitorBlockEntity.class,
            (player, monitor) -> monitor.handleInput(keycode, isDown));
    }
}
