/* SPDX-License-Identifier: MIT */

package li.cil.oc2.client;

import li.cil.oc2.client.gui.*;
import li.cil.oc2.client.item.CustomItemColors;
import li.cil.oc2.client.item.CustomItemModelProperties;
import li.cil.oc2.client.model.BusCableModelLoader;
import li.cil.oc2.client.renderer.BusInterfaceNameRenderer;
import li.cil.oc2.client.renderer.ProjectorDepthRenderer;
import li.cil.oc2.client.renderer.blockentity.*;
import li.cil.oc2.client.renderer.color.BusCableBlockColor;
import li.cil.oc2.client.renderer.entity.RobotRenderer;
import li.cil.oc2.client.renderer.entity.model.RobotModel;
import li.cil.oc2.common.block.Blocks;
import li.cil.oc2.common.blockentity.BlockEntities;
import li.cil.oc2.common.container.Containers;
import li.cil.oc2.common.entity.Entities;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.ModelEvent.RegisterGeometryLoaders;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

public final class ClientSetup {
    @SubscribeEvent
    public static void handleSetupEvent(final FMLClientSetupEvent event) {
        BusInterfaceNameRenderer.initialize();

        BlockEntityRenderers.register(BlockEntities.COMPUTER.get(), ComputerRenderer::new);
        BlockEntityRenderers.register(BlockEntities.MONITOR.get(), MonitorRenderer::new);
        BlockEntityRenderers.register(BlockEntities.DISK_DRIVE.get(), DiskDriveRenderer::new);
        BlockEntityRenderers.register(BlockEntities.CHARGER.get(), ChargerRenderer::new);
        BlockEntityRenderers.register(BlockEntities.PROJECTOR.get(), ProjectorRenderer::new);

        event.enqueueWork(() -> {
            CustomItemModelProperties.initialize();
            CustomItemColors.initialize();

            MenuScreens.register(Containers.COMPUTER.get(), ComputerContainerScreen::new);
            MenuScreens.register(Containers.COMPUTER_TERMINAL.get(), ComputerTerminalScreen::new);
            MenuScreens.register(Containers.MONITOR.get(), MonitorDisplayScreen::new);
            MenuScreens.register(Containers.ROBOT.get(), RobotContainerScreen::new);
            MenuScreens.register(Containers.ROBOT_TERMINAL.get(), RobotTerminalScreen::new);
            MenuScreens.register(Containers.NETWORK_TUNNEL.get(), NetworkTunnelScreen::new);

            //noinspection deprecation
            ItemBlockRenderTypes.setRenderLayer(Blocks.BUS_CABLE.get(), renderType -> true);
            Minecraft.getInstance().getBlockColors().register(new BusCableBlockColor(), Blocks.BUS_CABLE.get());

            // We need to register this manually, because static init throws errors when running data generation.
            MinecraftForge.EVENT_BUS.register(ProjectorDepthRenderer.class);
        });
    }

    @SubscribeEvent
    public static void handleModelRegistryEvent(final RegisterGeometryLoaders event) {
        event.register(Blocks.BUS_CABLE.getId().toString().replace("oc2:", ""), new BusCableModelLoader());
    }

    @SubscribeEvent
    public void renderHotbar(RenderGuiOverlayEvent event) {
        if(event.getOverlay().id() == VanillaGuiOverlay.HOTBAR.id() && KeyboardScreen.hideHotbar) {
            event.setCanceled(true);
        } else if(event.getOverlay().id() == VanillaGuiOverlay.HOTBAR.id()) {
            event.setCanceled(false);
        }
    }

    @SubscribeEvent
    public static void handleEntityRendererRegisterEvent(final EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(Entities.ROBOT.get(), RobotRenderer::new);
    }

    @SubscribeEvent
    public static void handleRegisterLayerDefinitionsEvent(final EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(RobotModel.ROBOT_MODEL_LAYER, RobotModel::createRobotLayer);
    }
}
