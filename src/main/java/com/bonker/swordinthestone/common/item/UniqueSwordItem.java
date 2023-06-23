package com.bonker.swordinthestone.common.item;

import com.bonker.swordinthestone.client.renderer.SSBEWLR;
import com.bonker.swordinthestone.util.AbilityUtil;
import com.bonker.swordinthestone.common.ability.SwordAbilities;
import com.bonker.swordinthestone.common.ability.SwordAbility;
import com.bonker.swordinthestone.util.Color;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.minecraftforge.common.ForgeTier;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;

public class UniqueSwordItem extends SwordItem {
    @SuppressWarnings("all") // suppress passing null for @NotNull tag
    public static final Tier TIER = new ForgeTier(0, 0, 0, 0, 10, null, () -> Ingredient.EMPTY);
    public static final HashBasedTable<UniqueSwordItem, SwordAbility, Color> STYLE_TABLE = HashBasedTable.create();
    private static final int BASE_DAMAGE = 7;
    private static final float BASE_SPEED = 1.2F - 4F;
    public static final String DAMAGE_TAG = "damage";
    public static final String SPEED_TAG = "speed";

    private static List<UniqueSwordItem> swords;
    private static List<SwordAbility> abilities;

    private final int color;

    public UniqueSwordItem(int color, Properties pProperties) {
        super(TIER, BASE_DAMAGE, BASE_SPEED, pProperties);
        this.color = color;
    }

    public int getColor() {
        return color;
    }

    public static ItemStack getRandom(String type, RandomSource random) {
        if (swords == null)
            swords = SSItems.ITEMS.getEntries().stream().filter(item -> item.get() instanceof UniqueSwordItem).map(item -> (UniqueSwordItem) item.get()).toList();
        if (abilities == null)
            abilities = SwordAbilities.SWORD_ABILITIES.getEntries().stream().map(RegistryObject::get).toList();

        SwordAbility ability = abilities.get(random.nextInt(abilities.size()));

        boolean randomItem = type.equals("random");
        UniqueSwordItem item;
        if (randomItem || !(ForgeRegistries.ITEMS.getValue(new ResourceLocation(type)) instanceof UniqueSwordItem uniqueSwordItem)) {
            item = swords.get(random.nextInt(swords.size()));
        } else {
            item = uniqueSwordItem;
        }

        ItemStack stack = new ItemStack(item);
        AbilityUtil.setSwordAbility(stack, ability);
        stack.getOrCreateTag().putFloat(UniqueSwordItem.DAMAGE_TAG, 7 + 0.5F * random.nextInt(7));
        stack.getOrCreateTag().putFloat(UniqueSwordItem.SPEED_TAG, 1.2F + 0.1F * random.nextInt(6));
        return stack;
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot slot, ItemStack stack) {
        if (slot != EquipmentSlot.MAINHAND) return super.getAttributeModifiers(slot, stack);
        float damage = stack.getOrCreateTag().getFloat(DAMAGE_TAG);
        float speed = stack.getOrCreateTag().getFloat(SPEED_TAG);
        return ImmutableMultimap.<Attribute, AttributeModifier>builder()
                .put(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "swordinthestone.attack_damage", damage > 0 ? damage : BASE_DAMAGE, AttributeModifier.Operation.ADDITION))
                .put(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_UUID, "swordinthestone.attack_speed", speed > 0 ? speed - 4F : BASE_SPEED, AttributeModifier.Operation.ADDITION))
                .build();
    }

    @Override
    public boolean hurtEnemy(ItemStack pStack, LivingEntity pTarget, LivingEntity pAttacker) {
        SwordAbility ability = AbilityUtil.getSwordAbility(pAttacker);
        if (pAttacker.level() instanceof ServerLevel serverLevel) {
            ability.hit(serverLevel, pAttacker, pTarget);
            if (pTarget.isDeadOrDying()) {
                ability.kill(serverLevel, pAttacker, pTarget);
            }
        }
        return true;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
        return AbilityUtil.getSwordAbility(pPlayer).use(pLevel, pPlayer, pUsedHand);
    }

    @Override
    public boolean isFoil(ItemStack pStack) {
        return super.isFoil(pStack) || AbilityUtil.getSwordAbility(pStack).hasGlint(pStack);
    }

    @Override
    public boolean isBarVisible(ItemStack pStack) {
        SwordAbility ability = AbilityUtil.getSwordAbility(pStack);
        return ability.isBarVisible(pStack);
    }

    @Override
    public int getBarWidth(ItemStack pStack) {
        SwordAbility ability = AbilityUtil.getSwordAbility(pStack);
        return ability.getBarWidth(pStack);
    }

    @Override
    public int getBarColor(ItemStack pStack) {
        SwordAbility ability = AbilityUtil.getSwordAbility(pStack);
        return ability.getBarColor(pStack);
    }

    @Override
    public void inventoryTick(ItemStack pStack, Level pLevel, Entity pEntity, int pSlotId, boolean pIsSelected) {
        AbilityUtil.getSwordAbility(pStack).inventoryTick(pStack, pLevel, pEntity, pSlotId, pIsSelected);
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return false;
    }

    @Override
    public Component getName(ItemStack pStack) {
        SwordAbility ability = AbilityUtil.getSwordAbility(pStack);
        if (ability == SwordAbility.NONE) return super.getName(pStack);
        Color color = STYLE_TABLE.get(this, ability);
        return Component.translatable(ability.getTitleKey(), super.getName(pStack)).withStyle(color == null ? Style.EMPTY : color.getStyle());
    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
        SwordAbility ability = AbilityUtil.getSwordAbility(pStack);
        if (ability != SwordAbility.NONE) {
            pTooltipComponents.add(Component.literal("★ ").append(Component.translatable(ability.getNameKey())).withStyle(ability.getColorStyle()));
            pTooltipComponents.add(Component.translatable(ability.getDescriptionKey()).withStyle(ChatFormatting.GRAY));
        }
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(SSBEWLR.extension());
    }
}
