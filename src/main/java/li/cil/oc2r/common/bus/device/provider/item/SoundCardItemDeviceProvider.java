/* SPDX-License-Identifier: MIT */

package li.cil.oc2r.common.bus.device.provider.item;

import li.cil.oc2r.api.bus.device.ItemDevice;
import li.cil.oc2r.api.bus.device.provider.ItemDeviceQuery;
import li.cil.oc2r.common.Config;
import li.cil.oc2r.common.bus.device.provider.util.AbstractItemDeviceProvider;
import li.cil.oc2r.common.bus.device.rpc.item.SoundCardItemDevice;
import li.cil.oc2r.common.item.Items;
import li.cil.oc2r.common.util.LocationSupplierUtils;

import java.util.Optional;

public final class SoundCardItemDeviceProvider extends AbstractItemDeviceProvider {
    public SoundCardItemDeviceProvider() {
        super(Items.SOUND_CARD);
    }

    ///////////////////////////////////////////////////////////////////

    @Override
    protected Optional<ItemDevice> getItemDevice(final ItemDeviceQuery query) {
        return Optional.of(new SoundCardItemDevice(query.getItemStack(), LocationSupplierUtils.of(query)));
    }

    @Override
    protected int getItemDeviceEnergyConsumption(final ItemDeviceQuery query) {
        return Config.soundCardEnergyPerTick;
    }
}
