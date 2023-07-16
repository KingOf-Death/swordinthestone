package com.bonker.swordinthestone.common.ability;

import com.bonker.swordinthestone.util.Color;
import com.google.common.collect.ImmutableMultimap;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
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

public abstract class SwordAbility {
    public static final SwordAbility NONE = new SwordAbility(0x000000) {};

    private final Color color;

    private String nameKey;
    private String titleKey;
    private String descriptionKey;

    public SwordAbility(int color) {
        this.color = new Color(color);
    }

    public int getColor() {
        return color.getIntColor();
    }

    public Style getColorStyle() {
        return color.getStyle();
    }

    public float[] getDiffusedColor() {
        return color.getDiffusedColor();
    }

    public void hit(ServerLevel level, LivingEntity holder, LivingEntity victim) {}

    public void kill(ServerLevel level, LivingEntity holder, LivingEntity victim) {}

    public InteractionResultHolder<ItemStack> use(Level level, Player holder, InteractionHand usedHand) {
        return InteractionResultHolder.pass(holder.getItemInHand(usedHand));
    }

    public boolean hasGlint(ItemStack stack) {return false;}

    public boolean isBarVisible(ItemStack pStack) {return false;}

    public int getBarWidth(ItemStack pStack) {return 0;}

    public int getBarColor(ItemStack pStack) {return color.getIntColor();}

    public void inventoryTick(ItemStack pStack, Level pLevel, Entity pEntity, int pSlotId, boolean pIsSelected) {}

    public void releaseUsing(ItemStack stack, Level level, LivingEntity entity, int ticks) {}

    public int getUseDuration() {return 0;}

    public UseAnim getUseAnimation() {return UseAnim.NONE;}

    public void addAttributes(ImmutableMultimap.Builder<Attribute, AttributeModifier> builder) {}

    public String getNameKey() {
        if (nameKey != null) return nameKey;
        ResourceLocation loc = SwordAbilities.SWORD_ABILITY_REGISTRY.get().getKey(this);
        if (loc == null) loc = new ResourceLocation("null");
        nameKey = "ability." + loc.getNamespace() + "." + loc.getPath();
        return nameKey;
    }

    public String getTitleKey() {
        if (titleKey != null) return titleKey;
        titleKey = getNameKey() + ".title";
        return titleKey;
    }

    public String getDescriptionKey() {
        if (descriptionKey != null) return descriptionKey;
        descriptionKey = getNameKey() + ".description";
        return descriptionKey;
    }
}
