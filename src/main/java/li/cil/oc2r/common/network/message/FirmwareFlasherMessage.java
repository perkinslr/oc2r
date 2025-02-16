/* SPDX-License-Identifier: MIT */

package li.cil.oc2r.common.network.message;

import li.cil.oc2r.common.blockentity.DiskDriveBlockEntity;
import li.cil.oc2r.common.blockentity.FlashMemoryFlasherBlockEntity;
import li.cil.oc2r.common.network.MessageUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

public final class FirmwareFlasherMessage extends AbstractMessage {
    private BlockPos pos;
    private CompoundTag data;

    ///////////////////////////////////////////////////////////////////

    public FirmwareFlasherMessage(final FlashMemoryFlasherBlockEntity diskDrive) {
        this.pos = diskDrive.getBlockPos();
        this.data = diskDrive.getFloppy().serializeNBT();
    }

    public FirmwareFlasherMessage(final FriendlyByteBuf buffer) {
        super(buffer);
    }

    ///////////////////////////////////////////////////////////////////

    @Override
    public void fromBytes(final FriendlyByteBuf buffer) {
        pos = buffer.readBlockPos();
        data = buffer.readNbt();
    }

    @Override
    public void toBytes(final FriendlyByteBuf buffer) {
        buffer.writeBlockPos(pos);
        buffer.writeNbt(data);
    }

    ///////////////////////////////////////////////////////////////////

    @Override
    protected void handleMessage(final NetworkEvent.Context context) {
        MessageUtils.withClientBlockEntityAt(pos, DiskDriveBlockEntity.class,
            diskDrive -> diskDrive.setFloppyClient(ItemStack.of(data)));
    }
}
