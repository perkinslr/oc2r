/* SPDX-License-Identifier: MIT */

package li.cil.oc2.common.blockentity;

import li.cil.oc2.api.API;
import li.cil.oc2.common.block.Blocks;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class BlockEntities {
    private static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, API.MOD_ID);

    ///////////////////////////////////////////////////////////////////

    public static final RegistryObject<BlockEntityType<BusCableBlockEntity>> BUS_CABLE = register(Blocks.BUS_CABLE, BusCableBlockEntity::new);
    public static final RegistryObject<BlockEntityType<ChargerBlockEntity>> CHARGER = register(Blocks.CHARGER, ChargerBlockEntity::new);
    public static final RegistryObject<BlockEntityType<ComputerBlockEntity>> COMPUTER = register(Blocks.COMPUTER, ComputerBlockEntity::new);
    public static final RegistryObject<BlockEntityType<MonitorBlockEntity>> MONITOR = register(Blocks.MONITOR, MonitorBlockEntity::new);
    public static final RegistryObject<BlockEntityType<CreativeEnergyBlockEntity>> CREATIVE_ENERGY = register(Blocks.CREATIVE_ENERGY, CreativeEnergyBlockEntity::new);
    public static final RegistryObject<BlockEntityType<DiskDriveBlockEntity>> DISK_DRIVE = register(Blocks.DISK_DRIVE, DiskDriveBlockEntity::new);
    public static final RegistryObject<BlockEntityType<FlashMemoryFlasherBlockEntity>> FLASH_MEMORY_FLASHER = register(Blocks.FLASH_MEMORY_FLASHER, FlashMemoryFlasherBlockEntity::new);
    public static final RegistryObject<BlockEntityType<KeyboardBlockEntity>> KEYBOARD = register(Blocks.KEYBOARD, KeyboardBlockEntity::new);
    public static final RegistryObject<BlockEntityType<NetworkConnectorBlockEntity>> NETWORK_CONNECTOR = register(Blocks.NETWORK_CONNECTOR, NetworkConnectorBlockEntity::new);
    public static final RegistryObject<BlockEntityType<NetworkHubBlockEntity>> NETWORK_HUB = register(Blocks.NETWORK_HUB, NetworkHubBlockEntity::new);
    public static final RegistryObject<BlockEntityType<NetworkSwitchBlockEntity>> NETWORK_SWITCH = register(Blocks.NETWORK_SWITCH, NetworkSwitchBlockEntity::new);
    public static final RegistryObject<BlockEntityType<ProjectorBlockEntity>> PROJECTOR = register(Blocks.PROJECTOR, ProjectorBlockEntity::new);
    public static final RegistryObject<BlockEntityType<RedstoneInterfaceBlockEntity>> REDSTONE_INTERFACE = register(Blocks.REDSTONE_INTERFACE, RedstoneInterfaceBlockEntity::new);
    public static final RegistryObject<BlockEntityType<VxlanBlockEntity>> VXLAN_HUB = register(Blocks.VXLAN_HUB, VxlanBlockEntity::new);
    public static final RegistryObject<BlockEntityType<PciCardCageBlockEntity>> PCI_CARD_CAGE = register(Blocks.PCI_CARD_CAGE, PciCardCageBlockEntity::new);


    ///////////////////////////////////////////////////////////////////

    public static void initialize() {
        BLOCK_ENTITIES.register(FMLJavaModLoadingContext.get().getModEventBus());
    }

    ///////////////////////////////////////////////////////////////////

    @SuppressWarnings("ConstantConditions") // .build(null) is fine
    private static <B extends Block, T extends BlockEntity> RegistryObject<BlockEntityType<T>> register(final RegistryObject<B> block, final BlockEntityType.BlockEntitySupplier<T> factory) {
        return BLOCK_ENTITIES.register(block.getId().getPath(), () -> BlockEntityType.Builder.of(factory, block.get()).build(null));
    }
}
