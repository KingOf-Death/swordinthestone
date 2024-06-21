package com.bonker.swordinthestone.client.gui;

import com.bonker.swordinthestone.common.ability.SwordAbility;
import com.bonker.swordinthestone.util.AbilityUtil;
import com.bonker.swordinthestone.util.Color;
import net.minecraft.client.renderer.RenderType;
import net.minecraftforge.client.IItemDecorator;

public class SSItemDecorator {
    public static final IItemDecorator ITEM_DECORATOR = (guiGraphics, font, stack, xOffset, yOffset) -> {
        SwordAbility ability = AbilityUtil.getSwordAbility(stack);

        if (ability.isBarVisible(stack)) {
            int width = ability.getBarWidth(stack);
            Color color = ability.getColor();

            int x = xOffset + 2;
            int y = yOffset + 13;
            if (stack.isBarVisible()) y -= 2;

            guiGraphics.fill(RenderType.guiOverlay(), x, y, x + 13, y + 2, color.getBG());
            guiGraphics.fill(RenderType.guiOverlay(), x, y, x + width, y + 1, color.getARGB());
        }
        return false;
    };
}
