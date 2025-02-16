/* SPDX-License-Identifier: MIT */

package li.cil.oc2r.common.bus.device.provider;

import li.cil.oc2r.api.bus.device.provider.BlockDeviceProvider;
import li.cil.oc2r.api.bus.device.provider.ItemDeviceProvider;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.function.BiConsumer;
import java.util.function.Supplier;

public final class Providers {
    public static IForgeRegistry<BlockDeviceProvider> blockDeviceProviderRegistry() {
        return ProviderRegistry.BLOCK_DEVICE_PROVIDER_REGISTRY.get();
    }

    public static IForgeRegistry<ItemDeviceProvider> itemDeviceProviderRegistry() {
        return ProviderRegistry.ITEM_DEVICE_PROVIDER_REGISTRY.get();
    }

    public static void registerBlockDeviceProviders(final BiConsumer<String, Supplier<BlockDeviceProvider>> registry) {

    }

    public static void registerItemDeviceProviders(final BiConsumer<String, Supplier<ItemDeviceProvider>> registry) {

    }
}
