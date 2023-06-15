package com.bonker.swordinthestone.common.ability;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
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

    public SwordAbility build() {
        return new BuiltSwordAbility(color, onHit, onKill, onUse, hasGlint, showBar, barWidth, barColor, inventoryTick);
    }

    private static class BuiltSwordAbility extends SwordAbility {
        private final HitAction onHit, onKill;
        private final UseAction onUse;
        private final Function<ItemStack, Boolean> hasGlint, showBar;
        private final Function<ItemStack, Integer> barWidth, barColor;
        private final TickAction inventoryTick;

        private BuiltSwordAbility(int color, @Nullable HitAction onHit, @Nullable HitAction onKill,
                                  @Nullable UseAction onUse, @Nullable Function<ItemStack, Boolean> hasGlint,
                                  @Nullable Function<ItemStack, Boolean> showBar, @Nullable Function<ItemStack, Integer> barWidth,
                                  @Nullable Function<ItemStack, Integer> barColor, @Nullable TickAction inventoryTick) {
            super(color);
            this.onHit = onHit;
            this.onKill = onKill;
            this.onUse = onUse;
            this.hasGlint = hasGlint;
            this.showBar = showBar;
            this.barWidth = barWidth;
            this.barColor = barColor;
            this.inventoryTick = inventoryTick;
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
}
