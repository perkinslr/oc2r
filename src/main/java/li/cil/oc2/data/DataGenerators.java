/* SPDX-License-Identifier: MIT */

package li.cil.oc2.data;

import li.cil.oc2.api.API;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Collections;
import java.util.Set;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public final class DataGenerators {
    @SubscribeEvent
    public static void gatherData(final GatherDataEvent event) {
        final DataGenerator generator = event.getGenerator();
        final ExistingFileHelper existingFileHelper = event.getExistingFileHelper();

        generator.addProvider(
            event.includeServer(),
            (DataProvider.Factory<LootTableProvider>) output -> new LootTableProvider(
                output,
                Set.of(),
                Collections.singletonList(
                    new LootTableProvider.SubProviderEntry(
                        ModLootTableProvider.ModBlockLootTables::new,
                        LootContextParamSets.BLOCK
                    )
                )
            )
        );
        var blockTagsProvider = generator.addProvider(event.includeServer(), (DataProvider.Factory<ModBlockTagsProvider>) output -> new ModBlockTagsProvider(output, event.getLookupProvider(), existingFileHelper));
        generator.addProvider(event.includeServer(), (DataProvider.Factory<ModItemTagsProvider>) output -> new ModItemTagsProvider(output, event.getLookupProvider(), blockTagsProvider.contentsGetter(), API.MOD_ID, existingFileHelper));
        generator.addProvider(event.includeServer(), (DataProvider.Factory<ModRecipesProvider>) ModRecipesProvider::new);
        generator.addProvider(event.includeClient(), new ModBlockStateProvider(generator.getPackOutput(), existingFileHelper));
        generator.addProvider(event.includeClient(), new ModItemModelProvider(generator.getPackOutput(), existingFileHelper));
    }
}
