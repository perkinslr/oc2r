/* SPDX-License-Identifier: MIT */

package li.cil.oc2r.common.bus.device.provider.item;

import li.cil.oc2r.api.bus.device.ItemDevice;
import li.cil.oc2r.api.bus.device.object.ObjectDevice;
import li.cil.oc2r.api.bus.device.provider.ItemDeviceQuery;
import li.cil.oc2r.common.bus.device.rpc.FluidHandlerDevice;
import li.cil.oc2r.common.capabilities.Capabilities;
import net.minecraftforge.fluids.capability.IFluidHandler;

import java.util.Optional;

public final class FluidHandlerItemDeviceProvider extends AbstractItemStackCapabilityDeviceProvider<IFluidHandler> {
    public FluidHandlerItemDeviceProvider() {
        super(Capabilities::fluidHandler);
    }

    ///////////////////////////////////////////////////////////////////

    @Override
    protected Optional<ItemDevice> getItemDevice(final ItemDeviceQuery query, final IFluidHandler value) {
        return Optional.of(new ObjectDevice(new FluidHandlerDevice(value)));
    }
}
