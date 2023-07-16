package com.bonker.swordinthestone.client.renderer.entity;

import com.bonker.swordinthestone.common.entity.SpellFireball;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemDisplayContext;

public class SpellFireballRenderer extends ThrownItemRenderer<SpellFireball> {
    private final ItemRenderer itemRenderer;

    public SpellFireballRenderer(EntityRendererProvider.Context pContext) {
        super(pContext);
        itemRenderer = pContext.getItemRenderer();
    }

    @Override
    public void render(SpellFireball pEntity, float pEntityYaw, float pPartialTicks, PoseStack pMatrixStack, MultiBufferSource pBuffer, int pPackedLight) {
        pMatrixStack.pushPose();
        float scale = pEntity.getPower() * 0.4F;
        pMatrixStack.translate(0, 0.5, 0);
        pMatrixStack.scale(scale, scale, scale);
        pMatrixStack.mulPose(entityRenderDispatcher.cameraOrientation());
        pMatrixStack.mulPose(Axis.YP.rotationDegrees(180.0F));
        itemRenderer.renderStatic(pEntity.getItem(), ItemDisplayContext.NONE, pPackedLight, OverlayTexture.NO_OVERLAY, pMatrixStack, pBuffer, pEntity.level(), pEntity.getId());
        pMatrixStack.popPose();
    }

    @Override
    protected int getBlockLightLevel(SpellFireball pEntity, BlockPos pPos) {
        if (pEntity.getPower() < 4.0F || System.currentTimeMillis() % 300 >= 150) return super.getBlockLightLevel(pEntity, pPos);
        return 15;
    }
}
