package com.bonker.swordinthestone.common;

import com.bonker.swordinthestone.SwordInTheStone;
import com.bonker.swordinthestone.client.particle.SSParticles;
import com.bonker.swordinthestone.common.ability.SwordAbilities;
import com.bonker.swordinthestone.common.ability.SwordAbility;
import com.bonker.swordinthestone.common.capability.DashCapability;
import com.bonker.swordinthestone.common.capability.ExtraJumpsCapability;
import com.bonker.swordinthestone.common.capability.ExtraJumpsProvider;
import com.bonker.swordinthestone.common.capability.IExtraJumpsCapability;
import com.bonker.swordinthestone.common.command.SSCommands;
import com.bonker.swordinthestone.common.entity.BatSwarmGoal;
import com.bonker.swordinthestone.common.entity.EnderRift;
import com.bonker.swordinthestone.common.entity.HeightAreaEffectCloud;
import com.bonker.swordinthestone.common.item.SSItems;
import com.bonker.swordinthestone.common.item.UniqueSwordItem;
import com.bonker.swordinthestone.common.networking.SSNetworking;
import com.bonker.swordinthestone.common.networking.ServerboundDashAttackPacket;
import com.bonker.swordinthestone.util.AbilityUtil;
import com.bonker.swordinthestone.util.Color;
import com.bonker.swordinthestone.util.DoubleJumpEvent;
import com.bonker.swordinthestone.util.Util;
import com.mojang.logging.LogUtils;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityAttributeModificationEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.*;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.registries.RegistryObject;
import org.slf4j.Logger;

public class CommonEvents {
    private static final Logger LOGGER = LogUtils.getLogger();

    @Mod.EventBusSubscriber(modid = SwordInTheStone.MODID)
    public static class ForgeBus {
        @SubscribeEvent
        public static void onCommandsRegistered(final RegisterCommandsEvent event) {
            SSCommands.register(event.getDispatcher());
        }

        @SubscribeEvent
        public static void onLivingAttack(final LivingAttackEvent event) {
            if (event.getEntity().invulnerableTime > 0) return;

            if (event.getSource().getDirectEntity() instanceof LivingEntity attacker && !(attacker instanceof Player)) {
                ItemStack stack =  attacker.getItemInHand(InteractionHand.MAIN_HAND);
                if (stack.getItem() instanceof UniqueSwordItem uniqueSwordItem)
                    uniqueSwordItem.hurtEnemy(stack, event.getEntity(), attacker);
            }

            if (event.getEntity() instanceof ServerPlayer player) {
                player.getCapability(DashCapability.DASH).ifPresent(cap -> {
                    if (DashCapability.getTicks(player) > 0 && event.getSource().is(DamageTypes.MOB_ATTACK)) event.setCanceled(true);
                });
            }
        }

        @SubscribeEvent
        public static void onAttachCapabilities(final AttachCapabilitiesEvent<Entity> event) {
            if (event.getObject() instanceof Player) {
                event.addCapability(DashCapability.NAME, DashCapability.createProvider());
                event.addCapability(ExtraJumpsCapability.NAME, new ExtraJumpsProvider());
            }
        }

        @SubscribeEvent
        public static void onPlayerTick(final TickEvent.PlayerTickEvent event) {
            Player player = event.player;
            player.getCapability(DashCapability.DASH).ifPresent(cap -> {
                if (event.phase == TickEvent.Phase.END) return;

                int dashTicks = DashCapability.getTicks(player);
                if (dashTicks <= 0) return;
                dashTicks--;

                if (player.getDeltaMovement().lengthSqr() < 0.01) dashTicks = 0;

                if (event.side == LogicalSide.SERVER && dashTicks > 0) {
                    HeightAreaEffectCloud.createToxicDashCloud(player.level(), player, player.getX(), player.getY() - 0.5, player.getZ());
                }

                if (dashTicks > 0 && event.side == LogicalSide.CLIENT) {
                    player.level().getEntities(player, player.getBoundingBox().inflate(0.5)).forEach(entity -> {
                        if (entity instanceof LivingEntity && !cap.isDashed(entity)) {
                            cap.addToDashed(entity);
                            SSNetworking.sendToServer(new ServerboundDashAttackPacket(entity.getId()));
                        }
                    });
                    cap.clearDashed();
                }
                cap.setDashTicks(dashTicks);
            });

            if (player.onGround()) {
                player.getCapability(ExtraJumpsCapability.JUMPS).ifPresent(IExtraJumpsCapability::resetExtraJumps);
            }
        }

        @SubscribeEvent
        public static void onLivingDeath(final LivingDeathEvent event) {
            if (event.getEntity() instanceof ServerPlayer player) {
                Util.getOwnedProjectiles(player, EnderRift.class, player.serverLevel()).forEach(Entity::discard);
            }
        }

        @SubscribeEvent
        public static void onLivingFall(final LivingFallEvent event) {
            if (AbilityUtil.isPassiveActive(event.getEntity(), SwordAbilities.DOUBLE_JUMP.get())) {
                float distance = event.getDistance();
                if (distance >= 3) {
                    if (distance <= 7) event.getEntity().playSound(SoundEvents.GENERIC_SMALL_FALL, 0.5F, 1.0F);

                    event.getEntity().playSound(SSSounds.LAND.get(), 0.3F, 0.6F + event.getEntity().level().random.nextFloat() * 0.8F);

                    if (event.getEntity().level() instanceof ServerLevel serverLevel) {
                        serverLevel.sendParticles(SSParticles.AIR.get(), event.getEntity().getX(), event.getEntity().getY() + 0.1, event.getEntity().getZ(), 20, 0.5, 0.1, 0.5, 0.05);
                    }
                }

                event.setDistance(distance - 4);
                event.setDamageMultiplier(0.6F);
            }
        }

        @SubscribeEvent
        public static void onDoubleJump(final DoubleJumpEvent event) {
            Vec3 pos = event.getEntity().position();
            ServerLevel level = (ServerLevel) event.getEntity().level();
            level.sendParticles(SSParticles.AIR.get(), pos.x, pos.y, pos.z, 20, 0.5, 0.1, 0.5, 0.05);
            level.playSound(null, pos.x, pos.y, pos.z, SSSounds.JUMP.get(), SoundSource.PLAYERS, 0.7F, 0.8F + event.getEntity().level().random.nextFloat() * 0.4F);
        }

        @SubscribeEvent
        public static void onEntityJoinLevel(final EntityJoinLevelEvent event) {
            if (event.loadedFromDisk() && event.getEntity().getTags().contains(BatSwarmGoal.BAT_SWARM)) {
                event.getEntity().setPos(Vec3.ZERO);
                event.getEntity().kill();
            }
        }

        @SubscribeEvent
        public static void onLivingDrops(final LivingDropsEvent event) {
            if (event.getEntity().getTags().contains(BatSwarmGoal.BAT_SWARM)) {
                event.setCanceled(true);
            }
        }

        @SubscribeEvent
        public static void onLivingExperienceDrop(final LivingExperienceDropEvent event) {
            if (event.getEntity().getTags().contains(BatSwarmGoal.BAT_SWARM)) {
                event.setCanceled(true);
            }
        }
    }

    @Mod.EventBusSubscriber(modid = SwordInTheStone.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ModBus {
        @SubscribeEvent
        public static void onCommonSetup(final FMLCommonSetupEvent event) {
            for (RegistryObject<Item> itemObj : SSItems.ITEMS.getEntries()) {
                if (itemObj.get() instanceof UniqueSwordItem uniqueSwordItem) {
                    for (RegistryObject<SwordAbility> abilityObj : SwordAbilities.SWORD_ABILITIES.getEntries()) {
                        UniqueSwordItem.STYLE_TABLE.put(uniqueSwordItem, abilityObj.get(), Color.uniqueSwordColor(abilityObj.get().getColor(), uniqueSwordItem.getColor()));
                    }
                }
            }
            LOGGER.info("Filled the Unique Sword text style table with {} combinations", UniqueSwordItem.STYLE_TABLE.size());
        }

        @SubscribeEvent
        public static void onAttributeModification(final EntityAttributeModificationEvent event) {
            event.add(EntityType.PLAYER, SSAttributes.JUMPS.get(), 0);
        }
    }
}
