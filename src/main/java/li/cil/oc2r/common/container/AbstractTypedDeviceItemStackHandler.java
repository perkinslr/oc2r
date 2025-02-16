/* SPDX-License-Identifier: MIT */

package li.cil.oc2r.common.container;

import li.cil.oc2r.api.bus.device.DeviceType;
import net.minecraft.world.item.ItemStack;

public abstract class AbstractTypedDeviceItemStackHandler extends AbstractDeviceItemStackHandler {
    private final DeviceType deviceType;

    ///////////////////////////////////////////////////////////////////

    public AbstractTypedDeviceItemStackHandler(final int size, final DeviceType deviceType) {
        super(size);
        this.deviceType = deviceType;
    }

    ///////////////////////////////////////////////////////////////////

    @Override
    public boolean isItemValid(final int slot, final ItemStack stack) {
        return super.isItemValid(slot, stack) && stack.is(deviceType.getTag());
    }
}
