package com.bonker.swordinthestone.client.gui;

import com.bonker.swordinthestone.common.ability.SwordAbility;
import com.bonker.swordinthestone.util.AbilityUtil;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.Mth;
import net.minecraftforge.client.IItemDecorator;

public class SSItemDecorator {
    public static final IItemDecorator ITEM_DECORATOR = (guiGraphics, font, stack, xOffset, yOffset) -> {
        SwordAbility ability = AbilityUtil.getSwordAbility(stack);
        float progress = ability.getProgress(stack);

        if (progress > 0) {
            if (ability.progressIsCooldown(stack)) {
                int minY = yOffset + Mth.floor(16 * progress);
                int maxY = minY + Mth.ceil(16 * (1 - progress));

                guiGraphics.fill(RenderType.guiOverlay(), xOffset, minY, xOffset + 16, maxY, ability.getColor().getCooldownColor());
            } else {
                guiGraphics.fill(RenderType.guiOverlay(), xOffset, yOffset + 11, xOffset + Math.round(16 * progress), yOffset + 16, -400, ability.getColor().getCooldownColor());
            }
        }
        return false;
    };
}
