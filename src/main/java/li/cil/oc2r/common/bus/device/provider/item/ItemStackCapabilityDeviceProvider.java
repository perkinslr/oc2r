/* SPDX-License-Identifier: MIT */

package li.cil.oc2r.common.bus.device.provider.item;

import li.cil.oc2r.api.bus.device.Device;
import li.cil.oc2r.api.bus.device.ItemDevice;
import li.cil.oc2r.api.bus.device.provider.ItemDeviceQuery;
import li.cil.oc2r.common.capabilities.Capabilities;

import java.util.Optional;

public class ItemStackCapabilityDeviceProvider extends AbstractItemStackCapabilityDeviceProvider<Device> {
    public ItemStackCapabilityDeviceProvider() {
        super(Capabilities::device);
    }

    ///////////////////////////////////////////////////////////////////

    @Override
    protected Optional<ItemDevice> getItemDevice(final ItemDeviceQuery query, final Device value) {
        if (value instanceof ItemDevice itemDevice) {
            return Optional.of(itemDevice);
        } else {
            return Optional.empty();
        }
    }
}
