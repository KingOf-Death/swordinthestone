package com.bonker.swordinthestone.common.item;

import com.bonker.swordinthestone.client.renderer.SSBEWLR;
import com.bonker.swordinthestone.common.SSConfig;
import com.bonker.swordinthestone.common.ability.SwordAbilities;
import com.bonker.swordinthestone.common.ability.SwordAbility;
import com.bonker.swordinthestone.util.AbilityUtil;
import com.bonker.swordinthestone.util.Color;
import com.bonker.swordinthestone.util.Util;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
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
    public static final Tier TIER = new ForgeTier(0, SSConfig.DURABILITY.get(), 0, 0, 10, BlockTags.MINEABLE_WITH_PICKAXE, () -> Ingredient.EMPTY);
    public static final HashBasedTable<UniqueSwordItem, SwordAbility, Color> COLOR_TABLE = HashBasedTable.create();
    public static final String DAMAGE_TAG = "damage";
    public static final String SPEED_TAG = "speed";

    private static List<UniqueSwordItem> swords;
    private static List<SwordAbility> abilities;

    private final int color;

    public UniqueSwordItem(int color, Properties pProperties) {
        super(TIER, SSConfig.BASE_DAMAGE.get() - 1, SSConfig.BASE_SPEED.get().floatValue() - 4, pProperties);
        this.color = color;
    }

    public int getColor() {
        return color;
    }

    public static ItemStack getRandom(String type, RandomSource random) {
        if (swords == null) {
            swords = SSItems.ITEMS.getEntries().stream().filter(item -> item.get() instanceof UniqueSwordItem).map(item -> (UniqueSwordItem) item.get()).toList();
        }
        if (abilities == null) {
            reloadAbilities();
        }

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
        stack.getOrCreateTag().putFloat(UniqueSwordItem.DAMAGE_TAG, SSConfig.BASE_DAMAGE.get() - 1 + Util.randomFloatMultiple(random, SSConfig.MAX_DAMAGE_MODIFIER.get().floatValue(), 0.5F));
        stack.getOrCreateTag().putFloat(UniqueSwordItem.SPEED_TAG, SSConfig.BASE_SPEED.get().floatValue() + Util.randomFloatMultiple(random, SSConfig.MAX_SPEED_MODIFIER.get().floatValue(), 0.1F));
        return stack;
    }

    public static void reloadAbilities() {
        abilities = SwordAbilities.SWORD_ABILITIES.getEntries().stream()
                .filter(obj -> !SSConfig.disabledAbilities.contains(obj.getId()))
                .map(RegistryObject::get)
                .toList();
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot slot, ItemStack stack) {
        if (slot != EquipmentSlot.MAINHAND) return super.getAttributeModifiers(slot, stack);
        float damage = stack.getOrCreateTag().getFloat(DAMAGE_TAG);
        float speed = stack.getOrCreateTag().getFloat(SPEED_TAG);
        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.<Attribute, AttributeModifier>builder()
                .put(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "swordinthestone.attack_damage", damage > 0 ? damage : SSConfig.BASE_DAMAGE.get() - 1, AttributeModifier.Operation.ADDITION))
                .put(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_UUID, "swordinthestone.attack_speed", speed > 0 ? speed - 4F : SSConfig.BASE_SPEED.get(), AttributeModifier.Operation.ADDITION));
        AbilityUtil.getSwordAbility(stack).addAttributes(builder);
        return builder.build();
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

        if (getTier().getUses() > 0) {
            return super.hurtEnemy(pStack, pTarget, pAttacker);
        }

        return true;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
        if (pUsedHand == InteractionHand.OFF_HAND) return InteractionResultHolder.pass(pPlayer.getItemInHand(pUsedHand));

        InteractionResultHolder<ItemStack> result = AbilityUtil.getSwordAbility(pPlayer).use(pLevel, pPlayer, pUsedHand);

        if (result.getResult() != InteractionResult.FAIL && getUseDuration(pPlayer.getItemInHand(pUsedHand)) > 0) {
            pPlayer.startUsingItem(pUsedHand);
            return InteractionResultHolder.consume(pPlayer.getItemInHand(pUsedHand));
        }

        return result;
    }

    @Override
    public void onUseTick(Level pLevel, LivingEntity pLivingEntity, ItemStack pStack, int pRemainingUseDuration) {
        AbilityUtil.getSwordAbility(pStack).useTick(pLevel, pLivingEntity, pStack, pRemainingUseDuration);
    }

    @Override
    public void inventoryTick(ItemStack pStack, Level pLevel, Entity pEntity, int pSlotId, boolean pIsSelected) {
        AbilityUtil.getSwordAbility(pStack).inventoryTick(pStack, pLevel, pEntity, pSlotId, pIsSelected);
    }

    @Override
    public void onStopUsing(ItemStack stack, LivingEntity entity, int useTime) {
        AbilityUtil.getSwordAbility(stack).releaseUsing(stack, entity.level(), entity, useTime);
    }

    @Override
    public int getUseDuration(ItemStack pStack) {
        return AbilityUtil.getSwordAbility(pStack).getUseDuration(pStack);
    }

    @Override
    public UseAnim getUseAnimation(ItemStack pStack) {
        return AbilityUtil.getSwordAbility(pStack).getUseAnimation(pStack);
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return false;
    }

    @Override
    public Component getName(ItemStack pStack) {
        SwordAbility ability = AbilityUtil.getSwordAbility(pStack);
        if (ability == SwordAbility.NONE) return super.getName(pStack);
        Color color = COLOR_TABLE.get(this, ability);
        return Component.translatable(ability.getTitleKey(), super.getName(pStack)).withStyle(color == null ? Style.EMPTY : color.getStyle());
    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
        SwordAbility ability = AbilityUtil.getSwordAbility(pStack);
        if (ability != SwordAbility.NONE) {
            pTooltipComponents.add(Component.literal("★ ").append(Component.translatable(ability.getNameKey())).withStyle(ability.getColorStyle()));
            pTooltipComponents.add(Component.translatable(ability.getDescriptionKey()).withStyle(ChatFormatting.GRAY));
            if (pStack.isEnchanted()) {
                pTooltipComponents.add(Component.empty());
            }
        }
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(SSBEWLR.extension());
    }
}
