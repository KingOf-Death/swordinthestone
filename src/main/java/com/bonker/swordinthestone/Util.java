package com.bonker.swordinthestone;

import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

public class Util {
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

        int r3 = (r1 + r2) / 3;
        int g3 = (g1 + g2) / 3;
        int b3 = (b1 + b2) / 3;

        int color3 = r3;
        color3 = (color3 << 8) + g3;
        color3 = (color3 << 8) + b3;
        return color3;
    }
}
