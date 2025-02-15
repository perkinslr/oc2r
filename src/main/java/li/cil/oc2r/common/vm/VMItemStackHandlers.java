/* SPDX-License-Identifier: MIT */

package li.cil.oc2r.common.vm;

import li.cil.oc2r.api.bus.device.DeviceType;
import net.minecraftforge.items.IItemHandler;

import java.util.Optional;

public interface VMItemStackHandlers {
    Optional<IItemHandler> getItemHandler(DeviceType deviceType);

    boolean isEmpty();

    void exportDeviceDataToItemStacks();
}
