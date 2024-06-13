package com.bonker.swordinthestone.client.gui;

import com.bonker.swordinthestone.SwordInTheStone;
import com.bonker.swordinthestone.common.block.SSBlocks;
import com.bonker.swordinthestone.common.block.SwordStoneBlock;
import com.bonker.swordinthestone.common.block.entity.ISwordStoneBlockEntity;
import com.bonker.swordinthestone.common.block.entity.SwordStoneMasterBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;

public class SSGuiOverlay {
    public static final String NAME = "swordinthestone_overlay";
    private static final ResourceLocation TEXTURE = new ResourceLocation(SwordInTheStone.MODID, "textures/gui/overlay.png");
    private static final int BAR_WIDTH = 94;
    private static final int BAR_HEIGHT = 18;

    public static final IGuiOverlay OVERLAY = (gui, guiGraphics, partialTick, screenWidth, screenHeight) -> {
        Minecraft minecraft = Minecraft.getInstance();

        if (minecraft.hitResult != null && minecraft.hitResult.getType() == HitResult.Type.BLOCK && minecraft.level != null) {
            BlockPos pos = ((BlockHitResult) minecraft.hitResult).getBlockPos();
            if (minecraft.level.getBlockState(pos).is(SSBlocks.SWORD_STONE.get())) {
                BlockEntity entity = minecraft.level.getBlockEntity(pos);
                if (entity instanceof ISwordStoneBlockEntity swordStone) {
                    SwordStoneMasterBlockEntity master = swordStone.getMaster();
                    if (master == null || !master.hasSword || minecraft.player == null || minecraft.player.isSpectator()) return;

                    if (master.cannotInteract()) {
                        guiGraphics.blit(TEXTURE, guiGraphics.guiWidth() / 2 + 2, guiGraphics.guiHeight() / 2 + 2, master.idleTicks % 40 > 20 ? 23 : 11, 0, 12, 12);
                    } else if (master.progress == 0) {
                        guiGraphics.blit(TEXTURE, guiGraphics.guiWidth() / 2 + 1, guiGraphics.guiHeight() / 2 + 1, 0, 0, 11, 18);
                    } else {
                        BlockState state = minecraft.level.getBlockState(pos);
                        if (state.hasProperty(SwordStoneBlock.VARIANT)) {
                            int progressPixels = Mth.ceil((float) (master.progress + 1) / SwordStoneMasterBlockEntity.REQUIRED_SHAKES * BAR_WIDTH);
                            int x = (guiGraphics.guiWidth() - BAR_WIDTH) / 2;
                            int y = guiGraphics.guiHeight() / 2 + 18;
                            int vOffset = 18 + state.getValue(SwordStoneBlock.VARIANT).ordinal() * BAR_HEIGHT * 2;

                            guiGraphics.blit(TEXTURE, x + progressPixels, y, progressPixels, vOffset, BAR_WIDTH - progressPixels, BAR_HEIGHT);
                            guiGraphics.blit(TEXTURE, x, y, 0, vOffset + BAR_HEIGHT, progressPixels, BAR_HEIGHT);
                        }
                    }
                }
            }
        }
    };
}
