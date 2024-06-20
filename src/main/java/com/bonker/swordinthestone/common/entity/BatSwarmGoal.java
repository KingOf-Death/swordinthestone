package com.bonker.swordinthestone.common.entity;

import com.bonker.swordinthestone.common.SSConfig;
import com.bonker.swordinthestone.common.networking.ClientboundSyncDeltaPacket;
import com.bonker.swordinthestone.common.networking.SSNetworking;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ambient.Bat;
import net.minecraft.world.phys.Vec3;

public class BatSwarmGoal extends Goal {
    public static final String BAT_SWARM = "swordinthestone.bat_swarm";

    private final Bat bat;
    private int ticks = 0;
    private final int lifetime;
    private final boolean isLeader;
    private final BatSwarm swarm;

    public BatSwarmGoal(Bat bat, BatSwarm swarm, boolean isLeader) {
        this.bat = bat;
        this.swarm = swarm;
        this.isLeader = isLeader;
        this.lifetime = SSConfig.BAT_SWARM_DURATION.get() - 5 + Math.round(bat.getRandom().nextFloat() * 10);
    }

    @Override
    public boolean canUse() {
        return bat.distanceToSqr(swarm.owner) < 400;
    }

    @Override
    public void start() {
        bat.addTag(BAT_SWARM);
    }

    @Override
    public void stop() {
        if (bat.level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.SMOKE, bat.getX(), bat.getY(), bat.getZ(), 10, 0.2, 0.2, 0.2, 0.1);
            bat.discard();
        }
    }

    @Override
    public void tick() {
        ticks++;

        if (bat.getRandom().nextFloat() > 0.9) bat.playAmbientSound();

        if (isLeader) {
            swarm.tick();
        }

        bat.setXRot(swarm.xRot);
        bat.setYRot(swarm.yRot);

        bat.level().getEntities(bat, bat.getBoundingBox().inflate(0.5), entity -> !(entity instanceof Bat)).forEach(entity -> {
            if (entity != swarm.owner && entity instanceof LivingEntity livingEntity) {
                livingEntity.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 100));
                entity.hurt(livingEntity.level().damageSources().mobAttack(bat), SSConfig.BAT_SWARM_DAMAGE.get().floatValue());
                entity.setDeltaMovement(swarm.hitDelta);
                if (entity instanceof ServerPlayer player) {
                    SSNetworking.sendToPlayer(new ClientboundSyncDeltaPacket(entity.getDeltaMovement()), player);
                }
            }
        });

        bat.setXRot(swarm.xRot);
        bat.setYRot(swarm.yRot);
        bat.setDeltaMovement(swarm.delta);
        bat.move(MoverType.SELF, bat.getDeltaMovement());

        if (bat.level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.ASH, bat.getX(), bat.getY(), bat.getZ(), 1, 0, 0, 0, 0);

            if ((ticks > 2 && !isMoving()) || ticks > lifetime) {
                stop();
            }
        }
    }

    private boolean isMoving() {
        return bat.getDeltaMovement().equals(swarm.delta);
    }

    public static class BatSwarm {
        public float xRot, yRot;
        public Vec3 delta;
        public Vec3 hitDelta;
        public Entity owner;

        public BatSwarm(Entity owner) {
            this.owner = owner;
        }

        public void tick() {
            xRot = owner.getXRot();
            yRot = owner.getYRot();
            delta = owner.getLookAngle().scale(0.45);
            hitDelta = delta.scale(1.5).add(0, 0.2, 0);
        }
    }
}
