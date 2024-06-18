package com.bonker.swordinthestone.common.ability;

import com.bonker.swordinthestone.util.AbilityUtil;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.function.Function;

public class SwordAbilityBuilder {
    private final int color;
    private HitAction onHit, onKill;
    private UseAction onUse;
    private Function<ItemStack, Boolean> hasGlint, showBar;
    private Function<ItemStack, Integer> barWidth, barColor;
    private TickAction inventoryTick;
    private ReleaseAction onRelease;
    private int useDuration = 0;
    private UseAnim useAnim = UseAnim.NONE;
    private Multimap<Attribute, AttributeModifier> attributes;

    public SwordAbilityBuilder(int color) {
        this.color = color;
    }

    public SwordAbilityBuilder onHit(HitAction onHit) {
        this.onHit = onHit;
        return this;
    }

    public SwordAbilityBuilder onKill(HitAction onKill) {
        this.onKill = onKill;
        return this;
    }

    public SwordAbilityBuilder onUse(UseAction onUse) {
        this.onUse = onUse;
        return this;
    }

    public SwordAbilityBuilder hasGlint(Function<ItemStack, Boolean> hasGlint) {
        this.hasGlint = hasGlint;
        return this;
    }

    public SwordAbilityBuilder customBar(Function<ItemStack, Boolean> showBar, Function<ItemStack, Integer> barWidth, Function<ItemStack, Integer> barColor) {
        this.showBar = showBar;
        this.barWidth = barWidth;
        this.barColor = barColor;
        return this;
    }

    public SwordAbilityBuilder inventoryTick(TickAction inventoryTick) {
        this.inventoryTick = inventoryTick;
        return this;
    }

    public SwordAbilityBuilder onReleaseUsing(ReleaseAction releaseAction) {
        this.onRelease = releaseAction;
        return this;
    }

    public SwordAbilityBuilder useDuration(int useDuration) {
        this.useDuration = useDuration;
        return this;
    }

    public SwordAbilityBuilder useAnimation(UseAnim useAnim) {
        this.useAnim = useAnim;
        return this;
    }

    public SwordAbilityBuilder attributes(Multimap<Attribute, AttributeModifier> attributes) {
        this.attributes = attributes;
        return this;
    }

    public SwordAbilityBuilder addCooldown(int time) {
        return customBar(stack -> AbilityUtil.showCooldownBar(stack, time),
                stack -> AbilityUtil.cooldownProgress(stack, time),
                null);
    }

    public SwordAbility build() {
        return new SwordAbilityImpl(color, onHit, onKill, onUse, hasGlint, showBar, barWidth, barColor, inventoryTick, onRelease, useDuration, useAnim, attributes);
    }

    private static class SwordAbilityImpl extends SwordAbility {
        private final HitAction onHit, onKill;
        private final UseAction onUse;
        private final Function<ItemStack, Boolean> hasGlint, showBar;
        private final Function<ItemStack, Integer> barWidth, barColor;
        private final TickAction inventoryTick;
        private final ReleaseAction onRelease;
        private final int useDuration;
        private final UseAnim useAnim;
        private final Multimap<Attribute, AttributeModifier> attributes;

        private SwordAbilityImpl(int color, @Nullable HitAction onHit, @Nullable HitAction onKill,
                                 @Nullable UseAction onUse, @Nullable Function<ItemStack, Boolean> hasGlint,
                                 @Nullable Function<ItemStack, Boolean> showBar, @Nullable Function<ItemStack, Integer> barWidth,
                                 @Nullable Function<ItemStack, Integer> barColor, @Nullable TickAction inventoryTick,
                                 @Nullable ReleaseAction onRelease, int useDuration, UseAnim useAnim,
                                 Multimap<Attribute, AttributeModifier> attributes) {
            super(color);
            this.onHit = onHit;
            this.onKill = onKill;
            this.onUse = onUse;
            this.hasGlint = hasGlint;
            this.showBar = showBar;
            this.barWidth = barWidth;
            this.barColor = barColor;
            this.inventoryTick = inventoryTick;
            this.onRelease = onRelease;
            this.useDuration = useDuration;
            this.useAnim = useAnim;
            this.attributes = attributes;
        }

        @Override
        public void hit(ServerLevel level, LivingEntity holder, LivingEntity victim) {
            if (onHit != null) onHit.run(level, holder, victim);
        }

        @Override
        public void kill(ServerLevel level, LivingEntity holder, LivingEntity victim) {
            if (onKill != null) onKill.run(level, holder, victim);
        }

        @Override
        public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
            if (onUse != null) return onUse.run(level, player, usedHand);
            return super.use(level, player, usedHand);
        }

        @Override
        public boolean hasGlint(ItemStack stack) {
            if (hasGlint != null) return hasGlint.apply(stack);
            return super.hasGlint(stack);
        }

        @Override
        public boolean isBarVisible(ItemStack stack) {
            if (showBar != null) return showBar.apply(stack);
            return super.isBarVisible(stack);
        }

        @Override
        public int getBarWidth(ItemStack stack) {
            if (barWidth != null) return barWidth.apply(stack);
            return super.getBarWidth(stack);
        }

        @Override
        public int getBarColor(ItemStack stack) {
            if (barColor != null) return barColor.apply(stack);
            return super.getBarColor(stack);
        }

        @Override
        public void inventoryTick(ItemStack pStack, Level pLevel, Entity pEntity, int pSlotId, boolean pIsSelected) {
            if (inventoryTick != null) inventoryTick.run(pStack, pLevel, pEntity, pSlotId, pIsSelected);
        }

        @Override
        public void releaseUsing(ItemStack stack, Level level, LivingEntity entity, int ticks) {
            if (onRelease != null) onRelease.run(stack, level, entity, ticks);
        }

        @Override
        public int getUseDuration() {
            return useDuration;
        }

        @Override
        public UseAnim getUseAnimation() {
            return useAnim;
        }

        @Override
        public void addAttributes(ImmutableMultimap.Builder<Attribute, AttributeModifier> builder) {
            if (attributes != null) {
                builder.putAll(attributes);
            }
        }
    }

    @FunctionalInterface
    public interface HitAction {
        void run(ServerLevel level, LivingEntity attacker, LivingEntity victim);
    }

    @FunctionalInterface
    public interface UseAction {
        InteractionResultHolder<ItemStack> run(Level level, Player player, InteractionHand usedHand);
    }

    @FunctionalInterface
    public interface TickAction {
        void run(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected);
    }

    @FunctionalInterface
    public interface ReleaseAction {
        void run(ItemStack stack, Level level, LivingEntity entity, int ticks);
    }
}
