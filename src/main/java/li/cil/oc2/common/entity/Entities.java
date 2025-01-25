/* SPDX-License-Identifier: MIT */

package li.cil.oc2.common.entity;

import li.cil.oc2.api.API;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Function;

public final class Entities {
    private static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, API.MOD_ID);

    ///////////////////////////////////////////////////////////////////

    public static final RegistryObject<EntityType<Robot>> ROBOT = register("robot", Robot::new, MobCategory.MISC, b -> b.sized(14f / 16f, 14f / 16f).fireImmune().noSummon());

    ///////////////////////////////////////////////////////////////////

    public static void initialize() {
        ENTITIES.register(FMLJavaModLoadingContext.get().getModEventBus());
    }

    ///////////////////////////////////////////////////////////////////

    private static <T extends Entity> RegistryObject<EntityType<T>> register(final String name, final EntityType.EntityFactory<T> factory, final MobCategory classification, final Function<EntityType.Builder<T>, EntityType.Builder<T>> customizer) {
        return ENTITIES.register(name, () -> customizer.apply(EntityType.Builder.of(factory, classification)).build(name));
    }
}
