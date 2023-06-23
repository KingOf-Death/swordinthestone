package com.bonker.swordinthestone.util;

import net.minecraft.network.chat.Style;

public class Color {
    private final int color;
    private Style style;
    private float[] diffusedColor;

    public Color(int color) {
        this.color = color;
    }

    public Style getStyle() {
        if (style == null) style = Style.EMPTY.withColor(color);
        return style;
    }

    public float[] getDiffusedColor() {
        if (diffusedColor == null) diffusedColor = MathUtil.diffuseColor(color);
        return diffusedColor;
    }
}
