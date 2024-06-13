package com.bonker.swordinthestone.common.ability;

import com.bonker.swordinthestone.SwordInTheStone;
import com.bonker.swordinthestone.client.particle.SSParticles;
import com.bonker.swordinthestone.common.SSAttributes;
import com.bonker.swordinthestone.common.SSSounds;
import com.bonker.swordinthestone.common.capability.DashCapability;
import com.bonker.swordinthestone.common.entity.BatSwarmGoal;
import com.bonker.swordinthestone.common.entity.EnderRift;
import com.bonker.swordinthestone.common.entity.SpellFireball;
import com.bonker.swordinthestone.common.networking.ClientboundSyncDeltaPacket;
import com.bonker.swordinthestone.common.networking.SSNetworking;
import com.bonker.swordinthestone.util.AbilityUtil;
import com.bonker.swordinthestone.util.Util;
import com.google.common.collect.ImmutableMultimap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ambient.Bat;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;
import net.minecraftforge.registries.RegistryObject;

import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

public class SwordAbilities {
    public static final DeferredRegister<SwordAbility> SWORD_ABILITIES = DeferredRegister.create(new ResourceLocation(SwordInTheStone.MODID, "sword_abilities"), SwordInTheStone.MODID);
    public static final Supplier<IForgeRegistry<SwordAbility>> SWORD_ABILITY_REGISTRY = SWORD_ABILITIES.makeRegistry(RegistryBuilder::new);

    // Thunder Smite
    public static final RegistryObject<SwordAbility> THUNDER_SMITE = register("thunder_smite",
            () -> new SwordAbilityBuilder(0x57faf6)
                    .onHit((level, holder, victim) -> {
                        ItemStack stack = holder.getItemInHand(InteractionHand.MAIN_HAND);
                        int charge = stack.getOrCreateTag().getInt("charge");
                        level.sendParticles(ParticleTypes.ELECTRIC_SPARK, victim.getX(), victim.getY() + 1.0, victim.getZ(), 20, 0.7, 1, 0.7, 0.4);
                        level.playSound(null, holder.getX(), holder.getY(), holder.getZ(), SSSounds.ZAP.get(), SoundSource.PLAYERS, 2.0F, 2.0F - charge * 0.5F);
                        if (++charge > 3) {
                            LightningBolt bolt = EntityType.LIGHTNING_BOLT.spawn(level, null, entity -> {
                                entity.setVisualOnly(true);
                                if (holder instanceof ServerPlayer serverPlayer) entity.setCause(serverPlayer);
                            }, victim.blockPosition(), MobSpawnType.MOB_SUMMONED, false, false);
                            if (bolt != null) {
                                List<Entity> list = level.getEntities(bolt, new AABB(bolt.getX() - 3.0D, bolt.getY() - 3.0D, bolt.getZ() - 3.0D, bolt.getX() + 3.0D, bolt.getY() + 6.0D + 3.0D, bolt.getZ() + 3.0D), Entity::isAlive);
                                for (Entity entity : list) {
                                    if (entity == holder) continue;
                                    if (!ForgeEventFactory.onEntityStruckByLightning(entity, bolt)) entity.thunderHit(level, bolt);
                                }
                            }
                            charge = 0;
                        }
                        stack.getOrCreateTag().putInt("charge", charge);
                    })
                    .hasGlint(stack -> stack.getOrCreateTag().getInt("charge") >= 3)
                    .build());
    // Vampiric
    public static final RegistryObject<SwordAbility> VAMPIRIC = register("vampiric",
            () -> new SwordAbilityBuilder(0xe20028)
                    .onKill((level, holder, victim) -> {
                        float healing = Mth.clamp(victim.getMaxHealth() * 0.2F, 1, 10);
                        int particles = Mth.clamp(Math.round(healing * 3), 4, 20);
                        holder.heal(healing);
                        level.sendParticles(SSParticles.HEAL.get(), victim.getX(), victim.getY() + victim.getBbHeight() * 0.5, victim.getZ(), particles, victim.getBbWidth() * 0.2, victim.getBbHeight() * 0.2, victim.getBbWidth() * 0.2, 0);
                    })
                    .build());
    // Toxic Dash
    public static final int TOXIC_DASH_COOLDOWN = 200;
    public static final RegistryObject<SwordAbility> TOXIC_DASH = register("toxic_dash",
            () -> new SwordAbilityBuilder(0x52c539)
                    .onUse((level, player, usedHand) -> {
                        ItemStack stack = player.getItemInHand(usedHand);
//                        if (!(player.onGround() || player.isUnderWater())) return InteractionResultHolder.pass(stack);
                        if (AbilityUtil.isOnCooldown(stack, level, TOXIC_DASH_COOLDOWN)) return InteractionResultHolder.fail(stack);

                        level.playSound(player, player.getX(), player.getY(), player.getZ(), SSSounds.DASH.get(), SoundSource.PLAYERS, 2.0F, 0.8F + level.random.nextFloat() * 0.4F);
                        level.playSound(player, player.getX(), player.getY(), player.getZ(), SSSounds.TOXIC.get(), SoundSource.PLAYERS, 2.0F, 0.8F + level.random.nextFloat() * 0.4F);

                        DashCapability.setTicks(player, 10);

                        Vec3 delta;
                        if (player.isUnderWater()) {
                            delta = Util.calculateViewVector(player.getXRot(), player.getYRot()).scale(2);
                        } else {
                            delta = Util.calculateViewVector(Math.min(0, player.getXRot()), player.getYRot()).multiply(3, 1.2, 3);
                        }

                        if (!level.isClientSide) {
                            player.push(delta.x, delta.y, delta.z);
                            SSNetworking.sendToPlayer(new ClientboundSyncDeltaPacket(player.getDeltaMovement()), (ServerPlayer) player);
                        }

                        AbilityUtil.setOnCooldown(stack, level, TOXIC_DASH_COOLDOWN);
                        return InteractionResultHolder.success(stack);
                    })
                    .inventoryTick((stack, level, entity, slotId, isSelected) -> AbilityUtil.updateCooldown(stack, level, TOXIC_DASH_COOLDOWN))
                    .customBar(AbilityUtil::showCooldownBar,
                               stack -> AbilityUtil.cooldownProgress(stack, TOXIC_DASH_COOLDOWN),
                               null)
                    .build());

    // Ender Rift
    public static final int ENDER_RIFT_DURATION = 60;
    public static final int ENDER_RIFT_COOLDOWN = 200; // must be more than the duration
    public static final RegistryObject<SwordAbility> ENDER_RIFT = register("ender_rift",
            () -> new SwordAbilityBuilder(0xe434ff)
                    .onUse((level, player, usedHand) -> {
                        ItemStack stack = player.getItemInHand(usedHand);
                        if (AbilityUtil.isOnCooldown(stack, level, ENDER_RIFT_COOLDOWN)) return InteractionResultHolder.fail(stack);

                        if (!level.isClientSide) {
                            EnderRift enderRift = new EnderRift(level, player);
                            level.addFreshEntity(enderRift);
                        }

                        level.playSound(player, player.getX(), player.getY(), player.getZ(), SSSounds.RIFT.get(), SoundSource.PLAYERS, 1.0F, 0.8F + level.random.nextFloat() * 0.4F);

                        return InteractionResultHolder.pass(stack);
                    })
                    .onReleaseUsing((stack, level, entity, ticks) -> {
                        if (AbilityUtil.isOnCooldown(stack, level, ENDER_RIFT_COOLDOWN)) return;

                        if (!level.isClientSide) {
                            if (ENDER_RIFT_DURATION - ticks > 4) {
                                Util.getOwnedProjectiles(entity, EnderRift.class, (ServerLevel) level).forEach(EnderRift::teleport);
                            } else {
                                Util.getOwnedProjectiles(entity, EnderRift.class, (ServerLevel) level).forEach(e -> e.getEntityData().set(EnderRift.DATA_CONTROLLING, false));
                            }
                        }

                        AbilityUtil.setOnCooldown(stack, level, ENDER_RIFT_COOLDOWN);
                    })
                    .useDuration(ENDER_RIFT_DURATION)
                    .inventoryTick((stack, level, entity, slotId, isSelected) -> AbilityUtil.updateCooldown(stack, level, ENDER_RIFT_COOLDOWN))
                    .customBar(AbilityUtil::showCooldownBar,
                            stack -> AbilityUtil.cooldownProgress(stack, ENDER_RIFT_COOLDOWN),
                            null)
                    .useAnimation(UseAnim.BLOCK)
                    .build());

    // Fireball
    public static final int FIREBALL_COOLDOWN = 120;
    public static final RegistryObject<SwordAbility> FIREBALL = register("fireball",
            () -> new SwordAbilityBuilder(0xff4b25)
                    .onUse((level, player, usedHand) -> {
                        ItemStack stack = player.getItemInHand(usedHand);
                        if (AbilityUtil.isOnCooldown(stack, level, FIREBALL_COOLDOWN)) return InteractionResultHolder.fail(stack);

                        if (!level.isClientSide) {
                            SpellFireball fireball = new SpellFireball(level, player);
                            fireball.setPower(0.1F);
                            fireball.setPos(player.getEyePosition().add(player.getLookAngle().scale(1.5)));
                            level.addFreshEntity(fireball);
                        }

                        return InteractionResultHolder.pass(stack);
                    })
                    .onReleaseUsing((stack, level, entity, ticks) -> {
                        if (!level.isClientSide) {
                            Util.getOwnedProjectiles(entity, SpellFireball.class, (ServerLevel) level).forEach(e -> e.getEntityData().set(SpellFireball.DATA_SHOT, true));
                        }
                        AbilityUtil.setOnCooldown(stack, level, FIREBALL_COOLDOWN);
                    })
                    .useDuration(72000)
                    .inventoryTick((stack, level, entity, slotId, isSelected) -> AbilityUtil.updateCooldown(stack, level, FIREBALL_COOLDOWN))
                    .customBar(AbilityUtil::showCooldownBar,
                            stack -> AbilityUtil.cooldownProgress(stack, FIREBALL_COOLDOWN),
                            null)
                    .useAnimation(UseAnim.BOW)
                    .build());

    // Double Jump
    public static final UUID DOUBLE_JUMP_UUID = new UUID(111222333, 444555666);
    public static final AttributeModifier DOUBLE_JUMP_MODIFIER = new AttributeModifier(DOUBLE_JUMP_UUID, "Double jump", 1, AttributeModifier.Operation.ADDITION);
    public static final RegistryObject<SwordAbility> DOUBLE_JUMP = register("double_jump",
            () -> new SwordAbilityBuilder(0xb2dce7)
                    .attributes(new ImmutableMultimap.Builder<Attribute, AttributeModifier>()
                            .put(SSAttributes.JUMPS.get(), DOUBLE_JUMP_MODIFIER)
                            .build())
                    .build());

    // Alchemist
    public static final List<Potion> POSITIVE_EFFECTS = List.of(Potions.FIRE_RESISTANCE, Potions.LEAPING, Potions.REGENERATION, Potions.WATER_BREATHING,
            Potions.NIGHT_VISION, Potions.SWIFTNESS, Potions.STRENGTH, Potions.STRONG_SWIFTNESS, Potions.STRONG_LEAPING, Potions.STRONG_REGENERATION, Potions.STRONG_STRENGTH);
    public static final List<Potion> NEGATIVE_EFFECTS = List.of(Potions.POISON, Potions.SLOWNESS, Potions.WEAKNESS, Potions.STRONG_POISON, Potions.STRONG_SLOWNESS);
    public static final RegistryObject<SwordAbility> ALCHEMIST = register("alchemist",
            () -> new SwordAbilityBuilder(0xffbf47)
            .onHit((level, attacker, victim) -> {
                if (victim.isDeadOrDying()) return;
                if (attacker instanceof Player player && player.getAttackStrengthScale(0F) < 1.0) return;

                if (level.random.nextFloat() > 0.5F) {
                    Potion potion = Util.randomListItem(NEGATIVE_EFFECTS, level.random);
                    MobEffectInstance effectInst = Util.copyWithDuration(potion.getEffects().get(0), 300);
                    victim.addEffect(effectInst);

                    if (attacker instanceof ServerPlayer player) {
                        AbilityUtil.sendAlchemistMsg(player, effectInst, false);
                    }

                    level.levelEvent(LevelEvent.PARTICLES_SPELL_POTION_SPLASH, BlockPos.containing(victim.position()), PotionUtils.getColor(potion));
                }
            })
            .onKill((level, attacker, victim) -> {
                if (level.random.nextFloat() > 0.5) {
                    Potion potion = Util.randomListItem(POSITIVE_EFFECTS, level.random);
                    MobEffectInstance effectInst = Util.copyWithDuration(potion.getEffects().get(0), 300);
                    attacker.addEffect(effectInst);

                    if (attacker instanceof ServerPlayer player) {
                        AbilityUtil.sendAlchemistMsg(player, effectInst, true);
                    }

                    level.levelEvent(LevelEvent.PARTICLES_SPELL_POTION_SPLASH, BlockPos.containing(attacker.position()), PotionUtils.getColor(potion));
                }
            })
            .build());

    // Bat Swarm
    public static final int BAT_SWARM_COOLDOWN = 200;
    public static final RegistryObject<SwordAbility> BAT_SWARM = register("bat_swarm",
            () -> new SwordAbilityBuilder(0xab29ff)
            .onUse((level, player, usedHand) -> {
                ItemStack stack = player.getItemInHand(InteractionHand.MAIN_HAND);
                if (AbilityUtil.isOnCooldown(stack, level, BAT_SWARM_COOLDOWN)) return InteractionResultHolder.fail(stack);

                if (!level.isClientSide) {
                    Vec3 pos = player.getEyePosition().add(player.getLookAngle().scale(0.9));
                    BatSwarmGoal.BatSwarm swarm = new BatSwarmGoal.BatSwarm(player);
                    for (int i = 0; i < 15; i++) {
                        boolean isLeader = i == 0;
                        Bat entity = EntityType.BAT.spawn((ServerLevel) level, null, bat -> {
                            bat.setPos(pos.add(Util.relativeVec(player.getRotationVector(), 0, (level.random.nextFloat() - 0.5) * 2 - 1, (level.random.nextFloat() - 0.5) * 2)));
                            bat.setCustomName(Component.translatable("ability.swordinthestone.bat_swarm.name", player.getDisplayName(), Component.translatable(bat.getType().getDescriptionId())).withStyle(SwordAbilities.BAT_SWARM.get().getColorStyle()));
                            bat.goalSelector.addGoal(0, new BatSwarmGoal(bat, swarm, isLeader));
                        }, BlockPos.ZERO, MobSpawnType.COMMAND,false, false);
                        if (isLeader && entity != null) player.startRiding(entity);
                    }
                }

                player.swing(InteractionHand.MAIN_HAND);
                AbilityUtil.setOnCooldown(stack, level, BAT_SWARM_COOLDOWN);
                return InteractionResultHolder.success(stack);
            })
            .inventoryTick((stack, level, entity, slotId, isSelected) -> AbilityUtil.updateCooldown(stack, level, BAT_SWARM_COOLDOWN))
            .customBar(AbilityUtil::showCooldownBar,
                    stack -> AbilityUtil.cooldownProgress(stack, BAT_SWARM_COOLDOWN),
                    null)
            .build());


    private static RegistryObject<SwordAbility> register(String name, Supplier<SwordAbility> supplier) {
        SwordInTheStone.ABILITY_MODEL_MAP.put(SwordInTheStone.MODID + ":" + name, new ResourceLocation(SwordInTheStone.MODID, "item/ability/" + name));
        return SWORD_ABILITIES.register(name, supplier);
    }
}
