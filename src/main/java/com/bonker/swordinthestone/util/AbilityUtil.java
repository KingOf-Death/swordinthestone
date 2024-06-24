package com.bonker.swordinthestone.util;

import com.bonker.swordinthestone.common.ability.SwordAbilities;
import com.bonker.swordinthestone.common.ability.SwordAbility;
import com.bonker.swordinthestone.common.item.UniqueSwordItem;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.function.Supplier;

public class AbilityUtil {
    public static SwordAbility getSwordAbility(LivingEntity holder) {
        return getSwordAbility(holder.getItemInHand(InteractionHand.MAIN_HAND));
    }

    public static SwordAbility getSwordAbility(ItemStack stack) {
        if (stack.getItem() instanceof UniqueSwordItem) {
            SwordAbility ability = SwordAbilities.SWORD_ABILITY_REGISTRY.get().getValue(new ResourceLocation(stack.getOrCreateTag().getString("ability")));
            return ability == null ? SwordAbility.NONE : ability;
        }
        return SwordAbility.NONE;
    }

    public static void setSwordAbility(ItemStack stack, SwordAbility ability) {
        ResourceLocation key = SwordAbilities.SWORD_ABILITY_REGISTRY.get().getKey(ability);
        if (key == null) return;
        stack.getOrCreateTag().putString("ability", key.toString());
    }

    public static boolean isPassiveActive(LivingEntity holder, SwordAbility ability) {
        return getSwordAbility(holder) == ability;
    }

    public static boolean isOnCooldown(ItemStack stack, @Nullable Level level, int cooldownLength) {
        long lastUsedTick = stack.getOrCreateTag().getInt("lastUsedTick");

        long time;
        if (level != null) {
            time = level.getGameTime() - lastUsedTick;
        } else {
            time = SideUtil.getTimeSinceTick(lastUsedTick);
        }

        return time < cooldownLength;
    }

    public static void setOnCooldown(ItemStack stack, Level level) {
        stack.getOrCreateTag().putLong("lastUsedTick", level.getGameTime());
    }

    public static float cooldownProgress(ItemStack stack, Supplier<Integer> cooldownSupplier) {
        long time = SideUtil.getTimeSinceTick(stack.getOrCreateTag().getInt("lastUsedTick"));
        int cooldown = cooldownSupplier.get();
        if (time >= cooldown) return 0;
        return (float) time / cooldown;
    }

    public static int getCharge(ItemStack stack) {
        return stack.getOrCreateTag().getInt("charge");
    }

    public static void setCharge(ItemStack stack, int charge) {
        stack.getOrCreateTag().putInt("charge", charge);
    }
}
