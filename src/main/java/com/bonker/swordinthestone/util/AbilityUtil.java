package com.bonker.swordinthestone.util;

import com.bonker.swordinthestone.common.ability.SwordAbilities;
import com.bonker.swordinthestone.common.ability.SwordAbility;
import com.bonker.swordinthestone.common.item.UniqueSwordItem;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;

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
        return Mth.clamp(Math.round(progress * 13.0F / maxProgress), 0, 13);
    }

    public static boolean isOnCooldown(ItemStack stack, @Nullable Level level, int cooldownLength) {
        return SideUtil.getTimeSinceTick(level, stack.getOrCreateTag().getInt("lastUsedTick")) < cooldownLength;
    }

    public static void setOnCooldown(ItemStack stack, Level level) {
        stack.getOrCreateTag().putLong("lastUsedTick", level.getGameTime());
    }

    public static boolean showCooldownBar(ItemStack stack, int cooldownLength) {
        return isOnCooldown(stack, null, cooldownLength);
    }

    public static int cooldownProgress(ItemStack stack, int cooldownLength) {
        return AbilityUtil.barProgress((int) SideUtil.getTimeSinceTick(null, stack.getOrCreateTag().getInt("lastUsedTick")), cooldownLength);
    }

    public static void sendAlchemistMsg(ServerPlayer player, MobEffectInstance effectInst, boolean self) {
        MutableComponent component = Component.translatable(effectInst.getDescriptionId());

        if (effectInst.getAmplifier() > 0) {
            component = Component.translatable("potion.withAmplifier", component, Component.translatable("potion.potency." + effectInst.getAmplifier()));
        }

        if (!effectInst.endsWithin(20)) {
            component = Component.translatable("potion.withDuration", component, Component.literal(effectInst.getDuration() / 20 + "s"));
        }

        component = component.withStyle(Style.EMPTY.withColor(effectInst.getEffect().getColor()));

        player.sendSystemMessage(Component.translatable("ability.swordinthestone.alchemist." + (self ? "self" : "victim"), component).withStyle(SwordAbilities.ALCHEMIST.get().getColorStyle()), true);
    }
}
