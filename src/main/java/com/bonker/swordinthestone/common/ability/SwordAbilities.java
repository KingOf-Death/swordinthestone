package com.bonker.swordinthestone.common.ability;

import com.bonker.swordinthestone.SwordInTheStone;
import com.bonker.swordinthestone.Util;
import com.bonker.swordinthestone.client.particle.SSParticles;
import com.bonker.swordinthestone.client.renderer.SSBEWLR;
import com.bonker.swordinthestone.common.SSSounds;
import com.bonker.swordinthestone.common.capability.DashCapability;
import com.bonker.swordinthestone.common.networking.ClientboundSyncDeltaPacket;
import com.bonker.swordinthestone.common.networking.SSNetworking;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;
import net.minecraftforge.registries.RegistryObject;

import java.util.List;
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
                                    if (!net.minecraftforge.event.ForgeEventFactory.onEntityStruckByLightning(entity, bolt)) entity.thunderHit(level, bolt);
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
                        int healing = 1 + level.random.nextInt(3);
                        int particles = switch (healing) {default -> 2; case 2 -> 6; case 3 -> 12;};
                        holder.heal(healing);
                        level.sendParticles(SSParticles.HEAL.get(), victim.getX(), victim.getY() + victim.getBbHeight() * 0.5, victim.getZ(), particles, victim.getBbWidth() * 0.2, victim.getBbHeight() * 0.2, victim.getBbWidth() * 0.2, 0);
                    })
                    .build());
    // Toxic Dash
    public static final int TOXIC_DASH_COOLDOWN = 10;
    public static final RegistryObject<SwordAbility> TOXIC_DASH = register("toxic_dash",
            () -> new SwordAbilityBuilder(0x52c539)
                    .onUse((level, player, usedHand) -> {
                        ItemStack stack = player.getItemInHand(usedHand);
                        if (!(player.onGround() || player.isUnderWater())) return InteractionResultHolder.pass(stack);
                        if (AbilityUtil.isOnCooldown(stack, level, TOXIC_DASH_COOLDOWN)) return InteractionResultHolder.fail(stack);

                        level.playSound(player, player.getX(), player.getY(), player.getZ(), SSSounds.DASH.get(), SoundSource.PLAYERS, 2.0F, 0.8F + level.random.nextFloat() * 0.4F);
                        level.playSound(player, player.getX(), player.getY(), player.getZ(), SSSounds.TOXIC.get(), SoundSource.PLAYERS, 2.0F, 0.8F + level.random.nextFloat() * 0.4F);

                        DashCapability.setTicks(player, 10);

                        Vec3 delta;
                        if (player.isUnderWater()) {
                            delta = Util.calculateViewVector(player.getXRot(), player.getYRot()).multiply(2, 2, 2);
                        } else {
                            delta = Util.calculateViewVector(Math.min(0, player.getXRot()), player.getYRot()).multiply(3, 1.2, 3);
                        }

                        player.push(delta.x, delta.y, delta.z);
                        if (!level.isClientSide) {
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




    private static RegistryObject<SwordAbility> register(String name, Supplier<SwordAbility> supplier) {
        /* TODO: figure out how to check for client */ SSBEWLR.ABILITY_MODEL_MAP.put(SwordInTheStone.MODID + ":" + name, new ResourceLocation(SwordInTheStone.MODID, "item/ability/" + name));
        return SWORD_ABILITIES.register(name, supplier);
    }
}
