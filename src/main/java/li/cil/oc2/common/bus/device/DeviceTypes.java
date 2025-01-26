/* SPDX-License-Identifier: MIT */

package li.cil.oc2.common.bus.device;

import li.cil.oc2.api.API;
import li.cil.oc2.api.bus.device.DeviceType;
import li.cil.oc2.common.bus.device.util.DeviceTypeImpl;
import li.cil.oc2.common.tags.ItemTags;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;

import java.util.function.Supplier;

import static li.cil.oc2.common.util.TranslationUtils.text;

public final class DeviceTypes {
    private static final DeferredRegister<DeviceType> DEVICE_TYPES = DeferredRegister.create(DeviceType.REGISTRY, API.MOD_ID);

    ///////////////////////////////////////////////////////////////////

    @SuppressWarnings("unused")
    public static final Supplier<IForgeRegistry<DeviceType>> DEVICE_TYPE_REGISTRY = DEVICE_TYPES.makeRegistry(RegistryBuilder::new);

    ///////////////////////////////////////////////////////////////////

    public static void initialize() {
        DEVICE_TYPES.register(FMLJavaModLoadingContext.get().getModEventBus());

        register(ItemTags.DEVICES_MEMORY);
        register(ItemTags.DEVICES_HARD_DRIVE);
        register(ItemTags.DEVICES_FLASH_MEMORY);
        register(ItemTags.DEVICES_CARD);
        register(ItemTags.DEVICES_ROBOT_MODULE);
        register(ItemTags.DEVICES_FLOPPY);
        register(ItemTags.DEVICES_NETWORK_TUNNEL);
        register(ItemTags.DEVICES_CPU);
    }

    ///////////////////////////////////////////////////////////////////

    private static void register(final TagKey<Item> tag) {
        final String id = tag.location().getPath().replaceFirst("^devices/", "");
        DEVICE_TYPES.register(id, () -> new DeviceTypeImpl(
            tag,
            new ResourceLocation(API.MOD_ID, "item/" + id + "_slot"),
            text("gui.{mod}.device_type." + id)
        ));
    }
}
