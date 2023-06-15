package com.bonker.swordinthestone.common.ability;

import com.bonker.swordinthestone.common.item.UniqueSwordItem;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

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

    public static int barProgress(int progress, int maxProgress) {
        return Mth.clamp(Math.round(13.0F - progress * 13.0F / maxProgress), 0, 13);
    }

    public static boolean isOnCooldown(ItemStack stack, Level level, int cooldownLength) {
        return stack.getOrCreateTag().getInt("lastUsedTick") + cooldownLength > level.getGameTime();
    }

    public static void setOnCooldown(ItemStack stack, Level level, int cooldownLength) {
        stack.getOrCreateTag().putInt("lastUsedTick", (int) level.getGameTime());
        stack.getOrCreateTag().putInt("cooldown", cooldownLength);
    }

    public static void updateCooldown(ItemStack stack, Level level, int cooldownLength) {
        stack.getOrCreateTag().putInt("cooldown", Mth.clamp((int) (stack.getOrCreateTag().getInt("lastUsedTick") + cooldownLength - level.getGameTime()), 0, cooldownLength));
    }

    public static boolean showCooldownBar(ItemStack stack) {
        return stack.getOrCreateTag().getInt("cooldown") > 0;
    }

    public static int cooldownProgress(ItemStack stack, int cooldownLength) {
        return AbilityUtil.barProgress(stack.getOrCreateTag().getInt("cooldown"), cooldownLength);
    }
}
