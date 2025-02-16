/* SPDX-License-Identifier: MIT */

package li.cil.oc2r.client.manual;

import li.cil.manual.api.ManualModel;
import li.cil.manual.api.Tab;
import li.cil.manual.api.prefab.Manual;
import li.cil.manual.api.prefab.provider.NamespaceDocumentProvider;
import li.cil.manual.api.prefab.provider.NamespacePathProvider;
import li.cil.manual.api.prefab.tab.ItemStackTab;
import li.cil.manual.api.prefab.tab.TextureTab;
import li.cil.manual.api.provider.DocumentProvider;
import li.cil.manual.api.provider.PathProvider;
import li.cil.manual.api.util.Constants;
import li.cil.oc2r.api.API;
import li.cil.oc2r.common.block.Blocks;
import li.cil.oc2r.common.item.Items;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

@OnlyIn(Dist.CLIENT)
public final class Manuals {
    private static final DeferredRegister<ManualModel> MANUALS = DeferredRegister.create(Constants.MANUAL_REGISTRY, Constants.MOD_ID);
    private static final DeferredRegister<PathProvider> PATH_PROVIDERS = DeferredRegister.create(Constants.PATH_PROVIDER_REGISTRY, Constants.MOD_ID);
    private static final DeferredRegister<DocumentProvider> CONTENT_PROVIDERS = DeferredRegister.create(Constants.DOCUMENT_PROVIDER_REGISTRY, Constants.MOD_ID);
    private static final DeferredRegister<Tab> TABS = DeferredRegister.create(Constants.TAB_REGISTRY, Constants.MOD_ID);

    ///////////////////////////////////////////////////////////////////

    public static final RegistryObject<ManualModel> MANUAL = MANUALS.register("manual", Manual::new);

    ///////////////////////////////////////////////////////////////////

    public static void initialize() {
        MANUALS.register(FMLJavaModLoadingContext.get().getModEventBus());

        PATH_PROVIDERS.register(FMLJavaModLoadingContext.get().getModEventBus());
        CONTENT_PROVIDERS.register(FMLJavaModLoadingContext.get().getModEventBus());
        TABS.register(FMLJavaModLoadingContext.get().getModEventBus());

        PATH_PROVIDERS.register("path_provider", () -> new NamespacePathProvider(API.MOD_ID));
        CONTENT_PROVIDERS.register("content_provider", () -> new NamespaceDocumentProvider(API.MOD_ID, "doc"));

        TABS.register("home", () -> new TextureTab(
            ManualModel.LANGUAGE_KEY + "/index.md",
            Component.translatable("manual." + API.MOD_ID + ".home"),
            new ResourceLocation(API.MOD_ID, "textures/gui/manual/home.png")));
        TABS.register("blocks", () -> new ItemStackTab(
            ManualModel.LANGUAGE_KEY + "/block/index.md",
            Component.translatable("manual." + API.MOD_ID + ".blocks"),
            new ItemStack(Blocks.COMPUTER.get())));
        TABS.register("modules", () -> new ItemStackTab(
            ManualModel.LANGUAGE_KEY + "/item/index.md",
            Component.translatable("manual." + API.MOD_ID + ".items"),
            new ItemStack(Items.TRANSISTOR.get())));
    }
}
