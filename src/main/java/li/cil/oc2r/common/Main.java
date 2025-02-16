/* SPDX-License-Identifier: MIT */

package li.cil.oc2r.common;

import dev.architectury.platform.forge.EventBuses;
import li.cil.ceres.Ceres;
import li.cil.oc2r.api.API;
import li.cil.oc2r.client.ClientSetup;
import li.cil.oc2r.client.manual.Manuals;
import li.cil.oc2r.common.block.Blocks;
import li.cil.oc2r.common.blockentity.BlockEntities;
import li.cil.oc2r.common.bus.device.DeviceTypes;
import li.cil.oc2r.common.bus.device.data.BlockDeviceDataRegistry;
import li.cil.oc2r.common.bus.device.data.FirmwareRegistry;
import li.cil.oc2r.common.bus.device.provider.ProviderRegistry;
import li.cil.oc2r.common.container.Containers;
import li.cil.oc2r.common.entity.Entities;
import li.cil.oc2r.common.item.Items;
import li.cil.oc2r.common.item.crafting.RecipeSerializers;
import li.cil.oc2r.common.serialization.ceres.Serializers;
import li.cil.oc2r.common.tags.BlockTags;
import li.cil.oc2r.common.tags.ItemTags;
import li.cil.oc2r.common.util.RegistryUtils;
import li.cil.oc2r.common.util.SoundEvents;
import li.cil.oc2r.common.vm.provider.DeviceTreeProviders;
import li.cil.sedna.Sedna;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(API.MOD_ID)
public final class Main {
    public Main() {
        EventBuses.registerModEventBus(API.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());
        Ceres.initialize();
        Sedna.initialize();
        DeviceTreeProviders.initialize();
        Serializers.initialize();

        ConfigManager.add(Config::new);
        ConfigManager.initialize();

        RegistryUtils.begin();

        ItemTags.initialize();
        BlockTags.initialize();
        Blocks.initialize();
        Items.initialize();
        BlockEntities.initialize();
        Entities.initialize();
        Containers.initialize();
        RecipeSerializers.initialize();
        SoundEvents.initialize();

        ProviderRegistry.initialize();
        DeviceTypes.initialize();

        BlockDeviceDataRegistry.initialize();
        FirmwareRegistry.initialize();

        RegistryUtils.finish();

        FMLJavaModLoadingContext.get().getModEventBus().register(CommonSetup.class);
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> Manuals::initialize);
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () ->
            FMLJavaModLoadingContext.get().getModEventBus().register(ClientSetup.class));
    }
}
