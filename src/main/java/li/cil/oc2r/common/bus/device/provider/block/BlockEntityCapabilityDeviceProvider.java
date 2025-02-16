/* SPDX-License-Identifier: MIT */

package li.cil.oc2r.common.bus.device.provider.block;

import li.cil.oc2r.api.bus.device.Device;
import li.cil.oc2r.api.bus.device.provider.BlockDeviceQuery;
import li.cil.oc2r.api.util.Invalidatable;
import li.cil.oc2r.common.bus.device.provider.util.AbstractBlockEntityCapabilityDeviceProvider;
import li.cil.oc2r.common.capabilities.Capabilities;
import net.minecraft.world.level.block.entity.BlockEntity;

public final class BlockEntityCapabilityDeviceProvider extends AbstractBlockEntityCapabilityDeviceProvider<Device, BlockEntity> {
    public BlockEntityCapabilityDeviceProvider() {
        super(Capabilities::device);
    }

    ///////////////////////////////////////////////////////////////////

    @Override
    protected Invalidatable<Device> getBlockDevice(final BlockDeviceQuery query, final Device device) {
        return Invalidatable.of(device);
    }
}
