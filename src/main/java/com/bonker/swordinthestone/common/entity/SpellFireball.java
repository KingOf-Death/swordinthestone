package com.bonker.swordinthestone.common.entity;

import com.bonker.swordinthestone.client.particle.SSParticles;
import com.bonker.swordinthestone.common.SSSounds;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.projectile.Fireball;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class SpellFireball extends Fireball {
    public static final EntityDataAccessor<Float> DATA_EXPLOSION_POWER = SynchedEntityData.defineId(SpellFireball.class, EntityDataSerializers.FLOAT);
    public static final EntityDataAccessor<Boolean> DATA_SHOT = SynchedEntityData.defineId(SpellFireball.class, EntityDataSerializers.BOOLEAN);
    private static final String POWER_KEY = "ExplosionPower";

    private int ticks = 0;

    public SpellFireball(EntityType<SpellFireball> entityType, Level level) {
        super(entityType, level);
    }

    public SpellFireball(Level level, Entity owner) {
        super(SSEntityTypes.SPELL_FIREBALL.get(), level);
        setOwner(owner);
    }

    @Override
    public void tick() {
        super.tick();

        boolean beenShot = !getDeltaMovement().equals(Vec3.ZERO);
        Entity owner = getOwner();

        if (!beenShot && owner != null) {
            Vec3 pos = owner.getEyePosition().add(owner.getLookAngle().scale(1.5));
            lerpTo(pos.x(), pos.y() - 0.5, pos.z(), getXRot(), getYRot(), 6, false);
        }

        if (getPower() <= 4.0F) {
            addPower(0.02F);
        }

        if (!beenShot && getEntityData().get(DATA_SHOT)) {
            playSound(SoundEvents.FIRECHARGE_USE, 1.0F, 1.0F);
            Vec3 vec = (owner == null ? this : owner).getLookAngle().scale(0.2);
            xPower = vec.x;
            yPower = vec.y;
            zPower = vec.z;
        }

        if (!beenShot && ticks++ % 37 == 0) {
            playSound(SSSounds.FIREBALL.get(), 1.0F, 1.0F);
        }
    }

    @Override
    protected void onHit(HitResult pResult) {
        super.onHit(pResult);
        if (!level().isClientSide) {
            level().explode(this, getX(), getY() + 0.5, getZ(), getPower(), true, Level.ExplosionInteraction.BLOCK);
            double radius = Math.min(1, getPower());
            ((ServerLevel) level()).sendParticles(SSParticles.FIRE.get(), getX(), getY(), getZ(), Mth.floor(radius * 25F), radius, radius, radius, 0F);
            discard();
        }
    }

    @Override
    protected void onHitEntity(EntityHitResult pResult) {
        super.onHitEntity(pResult);
        if (!level().isClientSide) {
            Entity entity = pResult.getEntity();
            if (entity instanceof EnderMan) {
                return;
            }
            Entity owner = getOwner();
            entity.hurt(damageSources().fireball(this, owner), 6.0F * getPower());
            if (owner instanceof LivingEntity) {
                doEnchantDamageEffects((LivingEntity) owner, entity);
            }
        }
    }

    public float getPower() {
        return getEntityData().get(DATA_EXPLOSION_POWER);
    }

    public void setPower(float power) {
        getEntityData().set(DATA_EXPLOSION_POWER, power);
    }

    public void addPower(float power) {
        setPower(getPower() + power);
    }

    @Override
    protected ParticleOptions getTrailParticle() {
        return SSParticles.FIRE.get();
    }

    @Override
    public void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        pCompound.putFloat(POWER_KEY, getPower());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        if (pCompound.contains(POWER_KEY, 5)) setPower(pCompound.getFloat(POWER_KEY));
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        getEntityData().define(DATA_EXPLOSION_POWER, 1.0F);
        getEntityData().define(DATA_SHOT, false);
    }
}
