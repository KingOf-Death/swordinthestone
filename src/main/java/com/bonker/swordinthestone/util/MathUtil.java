package com.bonker.swordinthestone.util;

import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

public class MathUtil {
    public static Vec3 calculateViewVector(float pXRot, float pYRot) {
        float f = pXRot * ((float)Math.PI / 180F);
        float f1 = -pYRot * ((float)Math.PI / 180F);
        float f2 = Mth.cos(f1);
        float f3 = Mth.sin(f1);
        float f4 = Mth.cos(f);
        float f5 = Mth.sin(f);
        return new Vec3(f3 * f4, -f5, f2 * f4);
    }

    public static int mergeColors(int color1, int color2) {
        int r1 = (color1 >> 16) & 0xFF;
        int g1 = (color1 >> 8) & 0xFF;
        int b1 = color1 & 0xFF;

        int r2 = (color2 >> 16) & 0xFF;
        int g2 = (color2 >> 8) & 0xFF;
        int b2 = color2 & 0xFF;

        int r3 = (r1 + r2) / 2;
        int g3 = (g1 + g2) / 2;
        int b3 = (b1 + b2) / 2;

        int color3 = r3;
        color3 = (color3 << 8) + g3;
        color3 = (color3 << 8) + b3;
        return color3;
    }

    public static float[] diffuseColor(int rgb) {
        int r = (rgb >> 16) & 0xFF;
        int g = (rgb >> 8) & 0xFF;
        int b = rgb & 0xFF;
        return new float[] {r / 255F, g / 255F, b / 255F};
    }

    public static class SwordSpinAnimation {
        private static final float[] swordSpinAnimation = net.minecraft.Util.make(new float[200], (floats) -> {
            for(int i = 0; i < floats.length; ++i) {
                floats[i] = swordSpinFunc(i);
            }
        });

        // represents a mathematical function: (paste into desmos) y\ =\ \frac{360\cdot6}{1+10^{-0.05\left(x-60\right)}}
        private static float swordSpinFunc(float animationTick) {
            return (360 * 4 / (1 + (float) Math.pow(10, -0.05 * (animationTick - 100))));
        }

        public static float swordSpin(float animationTick) {
            animationTick = Mth.clamp(animationTick, 0, swordSpinAnimation.length - 1);
            float value1 = swordSpinAnimation[Mth.floor(animationTick)];
            float value2 = swordSpinAnimation[Mth.ceil(animationTick)];
            float partialTick = animationTick % 1;
            return value1 + (value2 - value1) * partialTick;
        }
    }
}
