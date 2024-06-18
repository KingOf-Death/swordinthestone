package com.bonker.swordinthestone.util;

import com.bonker.swordinthestone.SwordInTheStone;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Streams;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.stream.Collectors;

public class Util {
    public static final EntityDataSerializer<Vec3> VEC3 = EntityDataSerializer.simple(Util::writeVec3, Util::readVec3);

    static {
        EntityDataSerializers.registerSerializer(VEC3);
    }

    public static void writeVec3(FriendlyByteBuf buf, Vec3 vec3) {
        buf.writeDouble(vec3.x());
        buf.writeDouble(vec3.y());
        buf.writeDouble(vec3.z());
    }

    public static Vec3 readVec3(FriendlyByteBuf buf) {
        return new Vec3(buf.readDouble(), buf.readDouble(), buf.readDouble());
    }

    public static Vec3 relativeVec(Vec2 rotation, double forwards, double up, double left) {
        float f = Mth.cos((rotation.y + 90.0F) * Mth.DEG_TO_RAD);
        float f1 = Mth.sin((rotation.y + 90.0F) * Mth.DEG_TO_RAD);
        float f2 = Mth.cos(-rotation.x * Mth.DEG_TO_RAD);
        float f3 = Mth.sin(-rotation.x * Mth.DEG_TO_RAD);
        float f4 = Mth.cos((-rotation.x + 90.0F) * Mth.DEG_TO_RAD);
        float f5 = Mth.sin((-rotation.x + 90.0F) * Mth.DEG_TO_RAD);
        Vec3 vec31 = new Vec3(f * f2, f3, f1 * f2);
        Vec3 vec32 = new Vec3(f * f4, f5, f1 * f4);
        Vec3 vec33 = vec31.cross(vec32).scale(-1.0D);
        double d0 = vec31.x * forwards + vec32.x * up + vec33.x * left;
        double d1 = vec31.y * forwards + vec32.y * up + vec33.y * left;
        double d2 = vec31.z * forwards + vec32.z * up + vec33.z * left;
        return new Vec3(d0, d1, d2);
    }

    public static Vec3 calculateViewVector(float pXRot, float pYRot) {
        float f = pXRot * ((float)Math.PI / 180F);
        float f1 = -pYRot * ((float)Math.PI / 180F);
        float f2 = Mth.cos(f1);
        float f3 = Mth.sin(f1);
        float f4 = Mth.cos(f);
        float f5 = Mth.sin(f);
        return new Vec3(f3 * f4, -f5, f2 * f4);
    }

    public static MobEffectInstance copyWithDuration(MobEffectInstance effect, int duration) {
        return new MobEffectInstance(effect.getEffect(), duration, effect.getAmplifier(), effect.isAmbient(), effect.isVisible(), effect.showIcon());
    }

    public static float[] diffuseColor(int rgb) {
        int r = (rgb >> 16) & 0xFF;
        int g = (rgb >> 8) & 0xFF;
        int b = rgb & 0xFF;
        return new float[] {r / 255F, g / 255F, b / 255F};
    }

    public static List<BlockPos> betweenClosed(BlockPos firstPos, BlockPos secondPos) {
        ImmutableList.Builder<BlockPos> builder = ImmutableList.builder();
        for (BlockPos blockPos : BlockPos.betweenClosed(firstPos, secondPos)) {
            builder.add(blockPos.immutable());
        }
        return builder.build();
    }

    @SuppressWarnings("unchecked")
    public static <T extends Projectile> List<T> getOwnedProjectiles(Entity owner, Class<T> clazz, ServerLevel level) {
        return Streams.stream(level.getAllEntities()).filter(clazz::isInstance)
                .map(entity -> (T) entity)
                .filter(e -> e.getOwner() == owner)
                .collect(Collectors.toList());
    }
    
    public static ResourceLocation makeResource(String path) {
        return new ResourceLocation(SwordInTheStone.MODID, path);
    }
    
    public static <T> TagKey<T> makeTag(ResourceKey<Registry<T>> registryKey, String path) {
        return TagKey.create(registryKey, Util.makeResource(path));
    }

    public static class SwordSpinAnimation {
        private static final float[] swordSpinAnimation = net.minecraft.Util.make(new float[200], (floats) -> {
            for(int i = 0; i < floats.length; ++i) {
                floats[i] = swordSpinFunc(i);
            }
        });

        // represents a mathematical function: (paste into desmos) y\ =\ \frac{360\cdot4}{1+10^{-0.05\left(x-100\right)}}
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
