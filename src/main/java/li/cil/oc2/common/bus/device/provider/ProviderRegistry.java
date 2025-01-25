/* SPDX-License-Identifier: MIT */

package li.cil.oc2.common.bus.device.provider;

import li.cil.oc2.api.API;
import li.cil.oc2.api.bus.device.provider.BlockDeviceProvider;
import li.cil.oc2.api.bus.device.provider.ItemDeviceProvider;
import li.cil.oc2.api.util.Registries;
import li.cil.oc2.common.bus.device.provider.block.BlockEntityCapabilityDeviceProvider;
import li.cil.oc2.common.bus.device.provider.item.*;
import li.cil.oc2.common.bus.device.rpc.block.*;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;

import java.util.function.Supplier;

public final class ProviderRegistry {
    private static final DeferredRegister<BlockDeviceProvider> BLOCK_DEVICE_PROVIDERS = DeferredRegister.create(Registries.BLOCK_DEVICE_PROVIDER, API.MOD_ID);
    public static final Supplier<IForgeRegistry<BlockDeviceProvider>> BLOCK_DEVICE_PROVIDER_REGISTRY = BLOCK_DEVICE_PROVIDERS.makeRegistry(RegistryBuilder::new);

    ///////////////////////////////////////////////////////////////////

    private static final DeferredRegister<ItemDeviceProvider> ITEM_DEVICE_PROVIDERS = DeferredRegister.create(Registries.ITEM_DEVICE_PROVIDER, API.MOD_ID);
    public static final Supplier<IForgeRegistry<ItemDeviceProvider>> ITEM_DEVICE_PROVIDER_REGISTRY = ITEM_DEVICE_PROVIDERS.makeRegistry(RegistryBuilder::new);

    ///////////////////////////////////////////////////////////////////

    public static void initialize() {
        ITEM_DEVICE_PROVIDERS.register(FMLJavaModLoadingContext.get().getModEventBus());
        BLOCK_DEVICE_PROVIDERS.register(FMLJavaModLoadingContext.get().getModEventBus());

        ITEM_DEVICE_PROVIDERS.register("memory", MemoryItemDeviceProvider::new);
        ITEM_DEVICE_PROVIDERS.register("hard_drive", HardDriveItemDeviceProvider::new);
        ITEM_DEVICE_PROVIDERS.register("hard_drive_custom", HardDriveWithExternalDataItemDeviceProvider::new);
        ITEM_DEVICE_PROVIDERS.register("flash_memory", FlashMemoryItemDeviceProvider::new);
        ITEM_DEVICE_PROVIDERS.register("flash_memory_custom", FlashMemoryWithExternalDataItemDeviceProvider::new);
        ITEM_DEVICE_PROVIDERS.register("redstone_interface_card", RedstoneInterfaceCardItemDeviceProvider::new);
        ITEM_DEVICE_PROVIDERS.register("network_interface_card", NetworkInterfaceCardItemDeviceProvider::new);
        ITEM_DEVICE_PROVIDERS.register("network_tunnel_card", NetworkTunnelCardItemDeviceProvider::new);
        ITEM_DEVICE_PROVIDERS.register("file_import_export_card", FileImportExportCardItemDeviceProvider::new);
        ITEM_DEVICE_PROVIDERS.register("sound_card", SoundCardItemDeviceProvider::new);
        ITEM_DEVICE_PROVIDERS.register("cpu", CPUItemDeviceProvider::new);

        ITEM_DEVICE_PROVIDERS.register("inventory_operations_module", InventoryOperationsModuleDeviceProvider::new);
        ITEM_DEVICE_PROVIDERS.register("block_operations_module", BlockOperationsModuleDeviceProvider::new);
        ITEM_DEVICE_PROVIDERS.register("network_tunnel_module", NetworkTunnelModuleItemDeviceProvider::new);

        ITEM_DEVICE_PROVIDERS.register("item_stack/capability", ItemStackCapabilityDeviceProvider::new);
        ITEM_DEVICE_PROVIDERS.register("energy_storage", EnergyStorageItemDeviceProvider::new);
        ITEM_DEVICE_PROVIDERS.register("fluid_handler", FluidHandlerItemDeviceProvider::new);
        ITEM_DEVICE_PROVIDERS.register("item_handler", ItemHandlerItemDeviceProvider::new);

        BLOCK_DEVICE_PROVIDERS.register("block", BlockStateObjectDeviceProvider::new);
        BLOCK_DEVICE_PROVIDERS.register("block_entity", BlockEntityObjectDeviceProvider::new);

        BLOCK_DEVICE_PROVIDERS.register("block_entity/capability", BlockEntityCapabilityDeviceProvider::new);
        BLOCK_DEVICE_PROVIDERS.register("energy_storage", EnergyStorageBlockDeviceProvider::new);
        BLOCK_DEVICE_PROVIDERS.register("fluid_handler", FluidHandlerBlockDeviceProvider::new);
        BLOCK_DEVICE_PROVIDERS.register("item_handler", ItemHandlerBlockDeviceProvider::new);
    }
}
