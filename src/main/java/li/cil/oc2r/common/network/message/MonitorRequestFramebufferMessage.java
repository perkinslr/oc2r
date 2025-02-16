/* SPDX-License-Identifier: MIT */

package li.cil.oc2r.common.network.message;

import li.cil.oc2r.common.blockentity.MonitorBlockEntity;
import li.cil.oc2r.common.network.MessageUtils;
import li.cil.oc2r.common.network.MonitorLoadBalancer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

public final class MonitorRequestFramebufferMessage extends AbstractMessage {
    private BlockPos pos;

    ///////////////////////////////////////////////////////////////////

    public MonitorRequestFramebufferMessage(final MonitorBlockEntity projector) {
        this.pos = projector.getBlockPos();
    }

    public MonitorRequestFramebufferMessage(final FriendlyByteBuf buffer) {
        super(buffer);
    }

    ///////////////////////////////////////////////////////////////////

    @Override
    public void fromBytes(final FriendlyByteBuf buffer) {
        pos = buffer.readBlockPos();
    }

    @Override
    public void toBytes(final FriendlyByteBuf buffer) {
        buffer.writeBlockPos(pos);
    }

    ///////////////////////////////////////////////////////////////////

    @Override
    protected void handleMessage(final NetworkEvent.Context context) {
        MessageUtils.withNearbyServerBlockEntity(context, pos, MonitorBlockEntity.class,
            (player, monitor) -> MonitorLoadBalancer.updateWatcher(monitor, player));
    }
}
