package com.bonker.swordinthestone.client.renderer;

import com.bonker.swordinthestone.common.block.blockentity.SwordStoneBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemDisplayContext;

public class SwordStoneBlockEntityRenderer implements BlockEntityRenderer<SwordStoneBlockEntity> {
    private final ItemRenderer itemRenderer;

    public SwordStoneBlockEntityRenderer(BlockEntityRendererProvider.Context pContext) {
        this.itemRenderer = pContext.getItemRenderer();
    }

    @Override
    public void render(SwordStoneBlockEntity pBlockEntity, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBufferSource, int pPackedLight, int pPackedOverlay) {
        RandomSource random = RandomSource.create(pBlockEntity.progress * 100000L); // create a random source dependent on progress so the output will be the same as long as the progress is the same
                                                                                    // this is necessary so the shake direction isn't different every frame
        pPoseStack.translate(1, 1.6, 1);
        pPoseStack.scale(1.3F, 1.3F, 1.3F);
        pPoseStack.mulPose(Axis.YP.rotationDegrees(45F));

        int direction = random.nextBoolean() ? 1 : -1; // shake direction (left/right)
        float tilt = direction * Mth.sin(pBlockEntity.ticksSinceLastInteraction + pPartialTick) * pBlockEntity.progress / pBlockEntity.ticksSinceLastInteraction;

        pPoseStack.rotateAround(Axis.ZP.rotationDegrees(tilt), 0, -0.5F, 0);
        pPoseStack.mulPose(Axis.ZP.rotationDegrees(-45F));

        itemRenderer.renderStatic(pBlockEntity.getItem(), ItemDisplayContext.FIXED, pPackedLight, pPackedOverlay, pPoseStack, pBufferSource, pBlockEntity.getLevel(), 42);
    }

    @Override
    public int getViewDistance() {
        return 128;
    }
}
