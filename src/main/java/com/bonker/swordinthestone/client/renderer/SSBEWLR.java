package com.bonker.swordinthestone.client.renderer;

import com.bonker.swordinthestone.SwordInTheStone;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.RenderTypeHelper;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.minecraftforge.registries.ForgeRegistries;
public class SSBEWLR extends BlockEntityWithoutLevelRenderer {
    public static SSBEWLR INSTANCE;
    public static IClientItemExtensions extension() {return new IClientItemExtensions() {
        @Override
        public BlockEntityWithoutLevelRenderer getCustomRenderer() {
            return SSBEWLR.INSTANCE;
        }
    };}

    public SSBEWLR(BlockEntityRenderDispatcher pBlockEntityRenderDispatcher, EntityModelSet pEntityModelSet) {
        super(pBlockEntityRenderDispatcher, pEntityModelSet);
    }

    @Override
    public void renderByItem(ItemStack pStack, ItemDisplayContext pDisplayContext, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight, int pPackedOverlay) {
        ResourceLocation swordModel = SwordInTheStone.SWORD_MODEL_MAP.get(ForgeRegistries.ITEMS.getKey(pStack.getItem()));
        ResourceLocation abilityModel = SwordInTheStone.ABILITY_MODEL_MAP.get(pStack.getOrCreateTag().getString("ability"));

        pPoseStack.popPose(); // remove translations from ItemRenderer
        pPoseStack.pushPose();

        render(pStack, swordModel, pDisplayContext, pPoseStack, pBuffer, RenderType.solid(), pStack.hasFoil(), pPackedLight, pPackedOverlay);
        if (abilityModel != null) render(pStack, abilityModel, pDisplayContext, pPoseStack, pBuffer, RenderType.translucent(), pStack.hasFoil(), pPackedLight, pPackedOverlay);
    }

    private static void render(ItemStack stack, ResourceLocation modelLoc, ItemDisplayContext displayContext, PoseStack poseStack, MultiBufferSource bufferSource, RenderType renderType, boolean glint, int packedLight, int packedOverlay) {
        poseStack.pushPose();

        ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();

        BakedModel model = Minecraft.getInstance().getModelManager().getModel(modelLoc);
        model = model.applyTransform(displayContext, poseStack, isLeftHand(displayContext));
        poseStack.translate(-0.5, -0.5, -0.5); // replicate ItemRenderer translation

        boolean inGui = displayContext == ItemDisplayContext.GUI;
        if (inGui) {
            Lighting.setupForFlatItems();
        }

        renderType = RenderTypeHelper.getEntityRenderType(renderType, true);
        VertexConsumer vertexConsumer = ItemRenderer.getFoilBuffer(bufferSource, renderType, true, glint);
        itemRenderer.renderModelLists(model, stack, packedLight, packedOverlay, poseStack, vertexConsumer);

if (pBuffer instanceof MultiBufferSource.BufferSource buf) {
    // normal rendering
} else {
    // fallback: accept Apotheosis' GhostBufferSource
    MultiBufferSource altBuf = (MultiBufferSource) pBuffer;
    // render using altBuf instead
}


        poseStack.popPose();
    }

    private static boolean isLeftHand(ItemDisplayContext displayContext) {
        return displayContext == ItemDisplayContext.FIRST_PERSON_LEFT_HAND || displayContext == ItemDisplayContext.THIRD_PERSON_LEFT_HAND;
    }
}
