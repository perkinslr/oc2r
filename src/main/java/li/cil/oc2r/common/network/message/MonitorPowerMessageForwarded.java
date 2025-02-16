/* SPDX-License-Identifier: MIT */

package li.cil.oc2r.common.network.message;

import li.cil.oc2r.common.blockentity.MonitorBlockEntity;
import li.cil.oc2r.common.network.MessageUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

public final class MonitorPowerMessageForwarded extends AbstractMessage {
    private BlockPos pos;
    private boolean power;

    ///////////////////////////////////////////////////////////////////

    public MonitorPowerMessageForwarded(final MonitorBlockEntity monitor, final boolean power) {
        this.pos = monitor.getBlockPos();
        this.power = power;
    }

    public MonitorPowerMessageForwarded(final FriendlyByteBuf buffer) {
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
        MessageUtils.withClientBlockEntityAt(pos, MonitorBlockEntity.class,
            (monitor) -> {
                if (power) {
                    monitor.start();
                } else {
                    monitor.stop();
                }
            });
    }
}
