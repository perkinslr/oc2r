/* SPDX-License-Identifier: MIT */

package li.cil.oc2.common.bus.device.provider;

import li.cil.oc2.api.bus.device.provider.BlockDeviceProvider;
import li.cil.oc2.api.bus.device.provider.ItemDeviceProvider;
import li.cil.oc2.common.bus.device.provider.block.BlockEntityCapabilityDeviceProvider;
import li.cil.oc2.common.bus.device.provider.item.*;
import li.cil.oc2.common.bus.device.rpc.block.*;
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
        registry.accept("memory", MemoryItemDeviceProvider::new);
        registry.accept("hard_drive", HardDriveItemDeviceProvider::new);
        registry.accept("hard_drive_custom", HardDriveWithExternalDataItemDeviceProvider::new);
        registry.accept("flash_memory", FlashMemoryItemDeviceProvider::new);
        registry.accept("flash_memory_custom", FlashMemoryWithExternalDataItemDeviceProvider::new);
        registry.accept("redstone_interface_card", RedstoneInterfaceCardItemDeviceProvider::new);
        registry.accept("network_interface_card", NetworkInterfaceCardItemDeviceProvider::new);
        registry.accept("network_tunnel_card", NetworkTunnelCardItemDeviceProvider::new);
        registry.accept("internet_card", InternetCardItemDeviceProvider::new);
        registry.accept("file_import_export_card", FileImportExportCardItemDeviceProvider::new);
        registry.accept("sound_card", SoundCardItemDeviceProvider::new);
    }
}
