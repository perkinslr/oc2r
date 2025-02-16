/* SPDX-License-Identifier: MIT */

package li.cil.oc2r.common.bus.device.provider.item;

import li.cil.oc2r.api.bus.device.ItemDevice;
import li.cil.oc2r.api.bus.device.provider.ItemDeviceQuery;
import li.cil.oc2r.common.Config;
import li.cil.oc2r.common.Constants;
import li.cil.oc2r.common.bus.device.provider.util.AbstractItemDeviceProvider;
import li.cil.oc2r.common.bus.device.vm.item.HardDriveDevice;
import li.cil.oc2r.common.item.HardDriveItem;
import li.cil.oc2r.common.util.LocationSupplierUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;
import java.util.Optional;

public final class HardDriveItemDeviceProvider extends AbstractItemDeviceProvider {
    public HardDriveItemDeviceProvider() {
        super(HardDriveItem.class);
    }

    ///////////////////////////////////////////////////////////////////

    @Override
    public void unmount(@Nullable final ItemDeviceQuery query, final CompoundTag tag) {
        super.unmount(query, tag);
        HardDriveDevice.unmount(tag);
    }

    ///////////////////////////////////////////////////////////////////

    @Override
    protected Optional<ItemDevice> getItemDevice(final ItemDeviceQuery query) {
        return Optional.of(new HardDriveDevice(query.getItemStack(), getCapacity(query), false, LocationSupplierUtils.of(query)));
    }

    @Override
    protected int getItemDeviceEnergyConsumption(final ItemDeviceQuery query) {
        return Math.max(1, (int) Math.round(getCapacity(query) * Config.hardDriveEnergyPerMegabytePerTick / Constants.MEGABYTE));
    }

    ///////////////////////////////////////////////////////////////////

    private static int getCapacity(final ItemDeviceQuery query) {
        final ItemStack stack = query.getItemStack();
        final HardDriveItem item = (HardDriveItem) stack.getItem();
        return Math.max(item.getCapacity(stack), 0);
    }
}
