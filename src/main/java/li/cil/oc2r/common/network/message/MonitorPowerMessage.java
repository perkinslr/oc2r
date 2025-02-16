/* SPDX-License-Identifier: MIT */

package li.cil.oc2r.common.network.message;

import li.cil.oc2r.common.blockentity.MonitorBlockEntity;
import li.cil.oc2r.common.network.MessageUtils;
import li.cil.oc2r.common.network.Network;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

public final class MonitorPowerMessage extends AbstractMessage {
    private BlockPos pos;
    private boolean power;

    ///////////////////////////////////////////////////////////////////

    public MonitorPowerMessage(final MonitorBlockEntity monitor, final boolean power) {
        this.pos = monitor.getBlockPos();
        this.power = power;
    }

    public MonitorPowerMessage(final FriendlyByteBuf buffer) {
        super(buffer);
    }

    ///////////////////////////////////////////////////////////////////

    @Override
    public void fromBytes(final FriendlyByteBuf buffer) {
        pos = buffer.readBlockPos();
        power = buffer.readBoolean();
    }

    @Override
    public void toBytes(final FriendlyByteBuf buffer) {
        buffer.writeBlockPos(pos);
        buffer.writeBoolean(power);
    }

    ///////////////////////////////////////////////////////////////////

    @Override
    protected void handleMessage(final NetworkEvent.Context context) {
        MessageUtils.withNearbyServerBlockEntityForInteraction(context, pos, MonitorBlockEntity.class,
            (player, monitor) -> {
                if (power) {
                    monitor.start();
                } else {
                    monitor.stop();
                }
                Network.sendToClientsTrackingBlockEntity(new MonitorPowerMessageForwarded(monitor, power), monitor);
            });
    }
}
