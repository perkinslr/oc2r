/* SPDX-License-Identifier: MIT */

package li.cil.oc2.client.renderer.blockentity;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.RemovalNotification;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import li.cil.oc2.api.API;
import li.cil.oc2.client.renderer.ModRenderType;
import li.cil.oc2.client.renderer.MonitorGUIRenderer;
import li.cil.oc2.common.Constants;
import li.cil.oc2.common.block.MonitorBlock;
import li.cil.oc2.common.blockentity.MonitorBlockEntity;
import li.cil.oc2.common.bus.device.vm.block.MonitorDevice;
import li.cil.oc2.common.util.ChainableVertexConsumer;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.Material;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.joml.Matrix4f;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = API.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class MonitorRenderer implements BlockEntityRenderer<MonitorBlockEntity> {
    public static final ResourceLocation OVERLAY_POWER_LOCATION = new ResourceLocation(API.MOD_ID, "block/monitor/monitor_overlay_power");
    public static final ResourceLocation OVERLAY_TERMINAL_LOCATION = new ResourceLocation(API.MOD_ID, "block/computer/computer_overlay_terminal");

    private static final Material TEXTURE_POWER = new Material(InventoryMenu.BLOCK_ATLAS, OVERLAY_POWER_LOCATION);
    private static final Material TEXTURE_TERMINAL = new Material(InventoryMenu.BLOCK_ATLAS, OVERLAY_TERMINAL_LOCATION);

    private static final Cache<MonitorGUIRenderer, MonitorGUIRenderer.RendererView> rendererViews = CacheBuilder.newBuilder()
        .expireAfterAccess(Duration.ofSeconds(5))
        .removalListener(MonitorRenderer::handleNoLongerRendering)
        .build();

    ///////////////////////////////////////////////////////////////////

    private final BlockEntityRenderDispatcher renderer;

    ///////////////////////////////////////////////////////////////////

    public MonitorRenderer(final BlockEntityRendererProvider.Context context) {
        this.renderer = context.getBlockEntityRenderDispatcher();
    }

    ///////////////////////////////////////////////////////////////////

    @Override
    public void render(final MonitorBlockEntity monitor, final float partialTicks, final PoseStack stack, final MultiBufferSource bufferSource, final int light, final int overlay) {
        final Direction blockFacing = monitor.getBlockState().getValue(MonitorBlock.FACING);
        final Vec3 cameraPosition = renderer.camera.getEntity().getEyePosition(partialTicks);

        // If viewer is not in front of the block we can skip the rest, it cannot be visible.
        // We check against the center of the block instead of the actual relevant face for simplicity.
        final Vec3 relativeCameraPosition = cameraPosition.subtract(Vec3.atCenterOf(monitor.getBlockPos()));
        final double projectedCameraPosition = relativeCameraPosition.dot(Vec3.atLowerCornerOf(blockFacing.getNormal()));
        if (projectedCameraPosition <= 0) {
            return;
        }

        stack.pushPose();

        // Align with front face of block.
        stack.translate(0.5f, 0, 0.5f);
        stack.mulPose(Axis.YN.rotationDegrees(blockFacing.toYRot() + 180));
        stack.translate(-0.5f, 0, -0.5f);

        // Flip and align with top left corner.
        stack.translate(1, 1, 0);
        stack.scale(-1, -1, -1);

        // Scale to make 1/16th of the block one unit and align with top left of terminal area.
        final float pixelScale = 1 / 16f;
        stack.scale(pixelScale, pixelScale, pixelScale);

        if (monitor.getPowerState() && monitor.isMounted() && monitor.hasPower()) {
            renderTerminal(monitor, stack, bufferSource, cameraPosition);
        } else if (monitor.getPowerState()) {
            renderStatusText(monitor, stack, cameraPosition);
        }

        stack.translate(0, 0, -0.1f);
        final Matrix4f matrix = stack.last().pose();

        if(monitor.getPowerState() && monitor.hasPower())
            renderPower(matrix, bufferSource);

        stack.popPose();
    }

    ///////////////////////////////////////////////////////////////////

    private void renderTerminal(final MonitorBlockEntity monitor, final PoseStack stack, final MultiBufferSource bufferSource, final Vec3 cameraPosition) {
        // Render terminal content if close enough.
        if (Vec3.atCenterOf(monitor.getBlockPos()).closerThan(cameraPosition, 6f)) {
            stack.pushPose();
            stack.translate(2, 2, -0.9f);

            // Scale to make terminal fit fully.
            final MonitorGUIRenderer terminal = monitor.getMonitor();
            final float textScaleX = 12f / MonitorDevice.WIDTH;
            final float textScaleY = 9f / MonitorDevice.HEIGHT;
            final float scale = Math.min(textScaleX, textScaleY) * 0.95f;

            // Center it on both axes.
            final float scaleDeltaX = textScaleX - scale;
            final float scaleDeltaY = textScaleY - scale;
            stack.translate(
                MonitorDevice.WIDTH * scaleDeltaX * 0.5f,
                MonitorDevice.HEIGHT * scaleDeltaY * 0.5f,
                0f);

            stack.scale(scale, scale, 1f);

            // TODO Make terminal renderer use buffer+rendertype.
            RenderSystem.enableBlend();
            RenderSystem.enableDepthTest();

            try {
                rendererViews.get(terminal, () -> terminal.getRenderer(monitor)).render(stack, RenderSystem.getProjectionMatrix(), MonitorDevice.WIDTH, MonitorDevice.HEIGHT);
            } catch (final ExecutionException e) {
                throw new RuntimeException(e);
            }

            stack.popPose();
        } else {
            stack.pushPose();
            stack.translate(0, 0, -0.9f);

            final Matrix4f matrix = stack.last().pose();
            renderQuad(matrix, TEXTURE_TERMINAL.buffer(bufferSource, ModRenderType::getUnlitBlock));

            stack.popPose();
        }
    }

    private void renderStatusText(final MonitorBlockEntity monitor, final PoseStack stack, final Vec3 cameraPosition) {
        if (!Vec3.atCenterOf(monitor.getBlockPos()).closerThan(cameraPosition, 12f)) {
            return;
        }

        final Component bootError = Component.translatable(Constants.COMPUTER_ERROR_NOT_ENOUGH_ENERGY);

        stack.pushPose();
        stack.translate(3, 3, -0.9f);

        drawText(stack, bootError);

        stack.popPose();
    }

    private void drawText(final PoseStack stack, final Component text) {
        final int maxWidth = 100;

        stack.pushPose();
        stack.scale(10f / maxWidth, 10f / maxWidth, 10f / maxWidth);

        final Font fontRenderer = renderer.font;
        final List<FormattedText> wrappedText = fontRenderer.getSplitter().splitLines(text, maxWidth, Style.EMPTY);
        if (wrappedText.size() == 1) {
            final int textWidth = fontRenderer.width(text);
            draw(fontRenderer, stack, text, (maxWidth - textWidth) * 0.5f, 0, 0xEE3322);
        } else {
            for (int i = 0; i < wrappedText.size(); i++) {
                draw(fontRenderer, stack, wrappedText.get(i).getString(), 0, i * fontRenderer.lineHeight, 0xEE3322);
            }
        }

        stack.popPose();
    }

    private void draw(Font font, PoseStack stack, Component text, float x, float y, int color) {
        var batch = MultiBufferSource.immediate(Tesselator.getInstance().getBuilder());
        font.drawInBatch(text, x, y, color, false, stack.last().pose(), batch, Font.DisplayMode.NORMAL, 0, 15728880);
        batch.endBatch();
    }

    private void draw(Font font, PoseStack stack, String text, float x, float y, int color) {
        var batch = MultiBufferSource.immediate(Tesselator.getInstance().getBuilder());
        font.drawInBatch(text, x, y, color, false, stack.last().pose(), batch, Font.DisplayMode.NORMAL, 0, 15728880, false);
        batch.endBatch();
    }

    private void renderPower(final Matrix4f matrix, final MultiBufferSource bufferSource) {
        renderQuad(matrix, TEXTURE_POWER.buffer(bufferSource, ModRenderType::getUnlitBlock));
    }

    private static void renderQuad(final Matrix4f matrix, final VertexConsumer consumer) {
        final VertexConsumer wrapper = new ChainableVertexConsumer(consumer);
        wrapper.vertex(matrix, 0, 0, 0)
            .uv(0, 0)
            .endVertex();

        wrapper.vertex(matrix, 0, 16, 0)
            .uv(0, 1)
            .endVertex();

        wrapper.vertex(matrix, 16, 16, 0)
            .uv(1, 1)
            .endVertex();

        wrapper.vertex(matrix, 16, 0, 0)
            .uv(1, 0)
            .endVertex();
    }

    @SubscribeEvent
    public static void updateCache(final TickEvent.ClientTickEvent event) {
        rendererViews.cleanUp();
    }

    private static void handleNoLongerRendering(final RemovalNotification<MonitorGUIRenderer, MonitorGUIRenderer.RendererView> notification) {
        final MonitorGUIRenderer key = notification.getKey();
        final MonitorGUIRenderer.RendererView value = notification.getValue();
        if (key != null && value != null) {
            key.releaseRenderer(value);
        }
    }
}
