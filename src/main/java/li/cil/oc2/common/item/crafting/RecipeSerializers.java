/* SPDX-License-Identifier: MIT */

package li.cil.oc2.common.item.crafting;

import li.cil.oc2.api.API;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class RecipeSerializers {
    private static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, API.MOD_ID);

    ///////////////////////////////////////////////////////////////////

    public static final RegistryObject<WrenchRecipe.Serializer> WRENCH = RECIPE_SERIALIZERS.register("wrench", () -> WrenchRecipe.Serializer.INSTANCE);

    ///////////////////////////////////////////////////////////////////

    public static void initialize() {
        RECIPE_SERIALIZERS.register(FMLJavaModLoadingContext.get().getModEventBus());
    }
}
