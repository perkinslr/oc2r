package li.cil.oc2r.common.item;

import li.cil.oc2r.api.API;
import net.minecraft.Util;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.MissingMappingsEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

public final class ItemRenameHandler {
    private static final Map<String, Supplier<Item>> RENAMES = Util.make(() -> {
        final Map<String, Supplier<Item>> map = new HashMap<>();

        map.put("hard_drive_buildroot", Items.HARD_DRIVE_CUSTOM::get);
        map.put("flash_memory_buildroot", Items.FLASH_MEMORY_CUSTOM::get);

        return map;
    });

    ///////////////////////////////////////////////////////////////////

    ///////////////////////////////////////////////////////////////////

    @SubscribeEvent
    private static void handleMissingMappings(final MissingMappingsEvent event) {

        for (final MissingMappingsEvent.Mapping mapping : event.getAllMappings(ResourceKey.createRegistryKey(event.getKey().location()))) {
            final ResourceLocation key = mapping.getKey();
            if (key == null || !Objects.equals(key.getNamespace(), API.MOD_ID)) {
                continue;
            }

            final Supplier<Item> replacement = RENAMES.get(key.getPath());
            if (replacement != null) {
                mapping.remap(replacement.get());
            }
        }
    }
}
