/* SPDX-License-Identifier: MIT */

package li.cil.oc2.data;

import li.cil.oc2.common.block.Blocks;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.CopyNbtFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.providers.nbt.ContextNbtProvider;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraftforge.registries.RegistryObject;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.Collections.singletonList;
import static li.cil.oc2.common.Constants.*;

public final class ModLootTableProvider extends LootTableProvider {
    public ModLootTableProvider(final PackOutput output, final Set<ResourceLocation> additionalTables, final List<SubProviderEntry> subProviders) {
        super(output, additionalTables, subProviders);
    }

    @Override
    public List<SubProviderEntry> getTables() {
        return singletonList(
            new LootTableProvider.SubProviderEntry(
                ModBlockLootTables::new,
                LootContextParamSets.BLOCK
            )
        );
    }

    public static final class ModBlockLootTables extends BlockLootSubProvider {
        public ModBlockLootTables() {
            super(Collections.emptySet(), FeatureFlags.REGISTRY.allFlags());
        }

        @Override
        protected void generate() {
            dropSelf(Blocks.CHARGER.get());
            add(Blocks.COMPUTER.get(), this::droppingWithInventory);
            dropSelf(Blocks.DISK_DRIVE.get());
            dropSelf(Blocks.KEYBOARD.get());
            dropSelf(Blocks.NETWORK_CONNECTOR.get());
            dropSelf(Blocks.NETWORK_HUB.get());
            dropSelf(Blocks.PROJECTOR.get());
            dropSelf(Blocks.REDSTONE_INTERFACE.get());
        }

        @Override
        protected Iterable<Block> getKnownBlocks() {
            return Blocks.BLOCKS.getEntries()
                .stream()
                .filter(blockRegObj -> blockRegObj.get() != Blocks.BUS_CABLE.get())
                .map(RegistryObject::get)
                .collect(Collectors.toList());
        }

        private LootTable.Builder droppingWithInventory(final Block block) {
            return LootTable.lootTable()
                .withPool(applyExplosionCondition(block, LootPool.lootPool()
                    .setRolls(ConstantValue.exactly(1))
                    .add(LootItem.lootTableItem(block)
                        .apply(CopyNbtFunction.copyData(ContextNbtProvider.BLOCK_ENTITY)
                            .copy(ITEMS_TAG_NAME,
                                concat(BLOCK_ENTITY_TAG_NAME_IN_ITEM, ITEMS_TAG_NAME),
                                CopyNbtFunction.MergeStrategy.REPLACE)
                            .copy(ENERGY_TAG_NAME,
                                concat(BLOCK_ENTITY_TAG_NAME_IN_ITEM, ENERGY_TAG_NAME),
                                CopyNbtFunction.MergeStrategy.REPLACE)
                        )
                    )
                ));
        }

        private static String concat(final String... paths) {
            return String.join(".", paths);
        }
    }
}
