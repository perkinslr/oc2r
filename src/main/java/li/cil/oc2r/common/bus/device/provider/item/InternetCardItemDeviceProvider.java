package li.cil.oc2r.common.bus.device.provider.item;

import li.cil.oc2r.api.bus.device.ItemDevice;
import li.cil.oc2r.api.bus.device.provider.ItemDeviceQuery;
import li.cil.oc2r.common.Config;
import li.cil.oc2r.common.bus.device.vm.item.InternetCardDevice;
import li.cil.oc2r.common.bus.device.provider.util.AbstractItemDeviceProvider;
import li.cil.oc2r.common.item.Items;

import java.util.Optional;

public final class InternetCardItemDeviceProvider extends AbstractItemDeviceProvider {
    public InternetCardItemDeviceProvider() {
        super(Items.INTERNET_CARD);
    }

    @Override
    protected Optional<ItemDevice> getItemDevice(final ItemDeviceQuery query) {
        return Optional.of(new InternetCardDevice(query.getItemStack()));
    }

    @Override
    protected int getItemDeviceEnergyConsumption(final ItemDeviceQuery query) {
        return Config.networkInterfaceEnergyPerTick;
    }
}
