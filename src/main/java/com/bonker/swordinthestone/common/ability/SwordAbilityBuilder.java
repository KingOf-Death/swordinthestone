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
import java.util.function.Supplier;

public class SwordAbilityBuilder {
    private final int color;
    private HitAction onHit, onKill;
    private UseAction onUse;
    private UseTickAction onUseTick;
    private Function<ItemStack, Boolean> isCooldown;
    private Function<ItemStack, Float> getProgress;
    private TickAction inventoryTick;
    private ReleaseAction onRelease;
    private Function<ItemStack, Integer> useDuration;
    private Function<ItemStack, UseAnim> useAnim;
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

    public SwordAbilityBuilder onUseTick(UseTickAction onUseTick) {
        this.onUseTick = onUseTick;
        return this;
    }

    public SwordAbilityBuilder customBar(Function<ItemStack, Boolean> isCooldown, Function<ItemStack, Float> getProgress) {
        this.isCooldown = isCooldown;
        this.getProgress = getProgress;
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

    public SwordAbilityBuilder useDuration(Function<ItemStack, Integer> useDuration) {
        this.useDuration = useDuration;
        return this;
    }

    public SwordAbilityBuilder useAnimation(Function<ItemStack, UseAnim> useAnim) {
        this.useAnim = useAnim;
        return this;
    }

    public SwordAbilityBuilder attributes(Multimap<Attribute, AttributeModifier> attributes) {
        this.attributes = attributes;
        return this;
    }

    public SwordAbilityBuilder addCooldown(Supplier<Integer> cooldownSupplier) {
        return customBar(stack -> true,
                stack -> AbilityUtil.cooldownProgress(stack, cooldownSupplier)
        );
    }

    public SwordAbility build() {
        return new SwordAbilityImpl(color, onHit, onKill, onUse, onUseTick, isCooldown, getProgress, inventoryTick, onRelease, useDuration, useAnim, attributes);
    }

    private static class SwordAbilityImpl extends SwordAbility {
        private final HitAction onHit, onKill;
        private final UseAction onUse;
        private final UseTickAction onUseTick;
        private final Function<ItemStack, Boolean> isCooldown;
        private final Function<ItemStack, Float> getProgress;
        private final TickAction inventoryTick;
        private final ReleaseAction onRelease;
        private final Function<ItemStack, Integer> useDuration;
        private final Function<ItemStack, UseAnim> useAnim;
        private final Multimap<Attribute, AttributeModifier> attributes;

        private SwordAbilityImpl(int color, @Nullable HitAction onHit, @Nullable HitAction onKill,
                                 @Nullable UseAction onUse, @Nullable UseTickAction onUseTick,
                                 @Nullable Function<ItemStack, Boolean> isCooldown, @Nullable Function<ItemStack, Float> getProgress,
                                 @Nullable TickAction inventoryTick, @Nullable ReleaseAction onRelease,
                                 @Nullable Function<ItemStack, Integer> useDuration, @Nullable Function<ItemStack, UseAnim> useAnim,
                                 @Nullable Multimap<Attribute, AttributeModifier> attributes) {
            super(color);
            this.onHit = onHit;
            this.onKill = onKill;
            this.onUse = onUse;
            this.onUseTick = onUseTick;
            this.isCooldown = isCooldown;
            this.getProgress = getProgress;
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
        public void useTick(Level level, LivingEntity livingEntity, ItemStack stack, int remainingUseDuration) {
            if (onUseTick != null) onUseTick.run(level, livingEntity, stack, remainingUseDuration);
        }

        @Override
        public float getProgress(ItemStack stack) {
            if (getProgress != null) return getProgress.apply(stack);
            return super.getProgress(stack);
        }

        @Override
        public boolean progressIsCooldown(ItemStack pStack) {
            if (isCooldown != null) return isCooldown.apply(pStack);
            return super.progressIsCooldown(pStack);
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
        public int getUseDuration(ItemStack stack) {
            if (useDuration == null) return 0;
            return useDuration.apply(stack);
        }

        @Override
        public UseAnim getUseAnimation(ItemStack stack) {
            if (useAnim == null) return UseAnim.NONE;
            return useAnim.apply(stack);
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
    public interface UseTickAction {
        void run(Level level, LivingEntity entity, ItemStack stack, int remainingUseDuration);
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
