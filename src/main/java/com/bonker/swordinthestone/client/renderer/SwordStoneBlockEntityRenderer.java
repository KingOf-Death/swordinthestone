package com.bonker.swordinthestone.client.renderer;

import com.bonker.swordinthestone.util.MathUtil;
import com.bonker.swordinthestone.common.block.entity.SwordStoneBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BeaconRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.phys.Vec3;

public class SwordStoneBlockEntityRenderer implements BlockEntityRenderer<SwordStoneBlockEntity> {
    private static final int HEIGHT_PER_TICK = 2;
//    private static final int BEAM_HEIGHT = 100;

    private final ItemRenderer itemRenderer;

    public SwordStoneBlockEntityRenderer(BlockEntityRendererProvider.Context pContext) {
        this.itemRenderer = pContext.getItemRenderer();
    }

    @Override
    public void render(SwordStoneBlockEntity entity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        if (!entity.getItem().isEmpty()) {
            float yRot = (entity.getBlockPos().getX() + entity.getBlockPos().getZ()) * 45F; // randomize base rotation
            if (entity.isIdle()) {
                renderBeacon(entity, partialTick, poseStack, bufferSource);
                yRot += MathUtil.SwordSpinAnimation.swordSpin(entity.idleTicks + partialTick + 20);
            }

            RandomSource random = RandomSource.create(entity.progress * 5000000L); // create a random source dependent on progress so the output will be the same as long as the progress is the same
            // this is necessary so the shake direction isn't different every frame

            float animationTick = entity.ticksSinceLastInteraction + partialTick;
            int direction = random.nextBoolean() ? 1 : -1; // shake direction (left/right)
            float tilt = direction * 1.5F * Mth.sin(animationTick) * entity.progress / (1 + animationTick * animationTick);

            poseStack.translate(1, 1.6, 1); // center the item
            poseStack.mulPose(Axis.YP.rotationDegrees(Mth.wrapDegrees(yRot))); // spinning angle
            poseStack.rotateAround(Axis.ZP.rotationDegrees(tilt), 0, -0.7F, 0); // shaking tilt
            poseStack.mulPose(Axis.ZP.rotationDegrees(-45F)); // sword sits upright

            itemRenderer.renderStatic(entity.getItem(), ItemDisplayContext.FIXED, packedLight, packedOverlay, poseStack, bufferSource, entity.getLevel(), 42);
        }
    }

    public void renderBeacon(SwordStoneBlockEntity entity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource) {
        poseStack.pushPose();
        poseStack.translate(0.5, 0, 0.5);

        long gameTime = entity.getLevel() == null ? 0 : entity.getLevel().getGameTime();
        int offset = -100 + Math.round((entity.idleTicks + partialTick - 75) * HEIGHT_PER_TICK); // -75 to send beacon in the middle of the spin animation
        int height = 100 + Math.min(0, offset);
        BeaconRenderer.renderBeaconBeam(poseStack, bufferSource, BeaconRenderer.BEAM_LOCATION, partialTick, 1.0F, gameTime, Math.max(0, offset), Math.max(0, height), entity.getBeamColor(), 0.10F, 0.115F);

        poseStack.popPose();
    }

    @Override
    public int getViewDistance() {
        return 256;
    }

    @Override
    public boolean shouldRenderOffScreen(SwordStoneBlockEntity pBlockEntity) {
        return true;
    }

    @Override
    public boolean shouldRender(SwordStoneBlockEntity pBlockEntity, Vec3 pCameraPos) {
        return Vec3.atCenterOf(pBlockEntity.getBlockPos()).multiply(1.0D, 0.0D, 1.0D).closerThan(pCameraPos.multiply(1.0D, 0.0D, 1.0D), getViewDistance());
    }
}
