package com.bonker.swordinthestone.common.entity;

import com.bonker.swordinthestone.common.ability.SwordAbilities;
import com.google.common.collect.Lists;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.List;

public class HeightAreaEffectCloud extends AreaEffectCloud {
    private static final DustParticleOptions TOXIC_DASH_PARTICLE = new DustParticleOptions(Vec3.fromRGB24(SwordAbilities.TOXIC_DASH.get().getColor()).toVector3f(), 1.25F);
    private static final EntityDataAccessor<Float> DATA_HEIGHT = SynchedEntityData.defineId(HeightAreaEffectCloud.class, EntityDataSerializers.FLOAT);
    private boolean ownerImmune = false;

    public HeightAreaEffectCloud(EntityType<? extends HeightAreaEffectCloud> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public HeightAreaEffectCloud(Level pLevel, double pX, double pY, double pZ) {
        this(SSEntityTypes.HEIGHT_AREA_EFFECT_CLOUD.get(), pLevel);
        setPos(pX, pY, pZ);
    }

    public static void createToxicDashCloud(Level level, @Nullable LivingEntity owner, double x, double y, double z) {
        HeightAreaEffectCloud cloud = new HeightAreaEffectCloud(level, x, y, z);
        cloud.setOwner(owner);
        cloud.setOwnerImmune(true);
        cloud.setRadius(1F);
        cloud.setHeight(2.5F);
        cloud.setRadiusOnUse(-1F);
        cloud.setWaitTime(0);
        cloud.setRadiusPerTick(0);
        cloud.setDuration(60);
        cloud.setPotion(Potions.POISON);
        cloud.setParticle(TOXIC_DASH_PARTICLE);
        level.addFreshEntity(cloud);
    }

    public void tick() {
        this.baseTick();

        boolean waiting = isWaiting();
        float radius = getRadius();
        if (level().isClientSide) {
            if (waiting) {
                return;
            }

            ParticleOptions particle = getParticle();
            float height = getHeight();
            int particleCount = Mth.ceil((float) Math.PI * radius * radius * height * 0.5);

            for (int i = 0; i < particleCount; ++i) {
                float radians = random.nextFloat() * ((float) Math.PI * 2F);
                float distance = Mth.sqrt(random.nextFloat()) * radius;
                double x = getX() + (double)(Mth.cos(radians) * distance);
                double y = getY() + height * random.nextFloat();
                double z = getZ() + (double)(Mth.sin(radians) * distance);
                double xd;
                double yd;
                double zd;
                if (particle.getType() == ParticleTypes.ENTITY_EFFECT) {
                    int color = getColor();
                    xd = (float)(color >> 16 & 255) / 255.0F;
                    yd = (float)(color >> 8 & 255) / 255.0F;
                    zd = (float)(color & 255) / 255.0F;
                } else {
                    xd = (0.5D - random.nextDouble()) * 0.15D;
                    yd = 0.01F;
                    zd = (0.5D - random.nextDouble()) * 0.15D;
                }

                level().addAlwaysVisibleParticle(particle, x, y, z, xd, yd, zd);
            }
        } else {
            if (tickCount >= waitTime + duration) {
                discard();
                return;
            }

            boolean stillWaiting = tickCount < waitTime;
            if (waiting != stillWaiting) {
                setWaiting(stillWaiting);
            }

            if (stillWaiting) {
                return;
            }

            if (radiusPerTick != 0.0F) {
                radius += radiusPerTick;
                if (radius < 0.1F) {
                    discard();
                    return;
                }

                setRadius(radius);
            }

            if (tickCount % 5 == 0) {
                victims.entrySet().removeIf(entry -> tickCount >= entry.getValue());
                List<MobEffectInstance> effects = Lists.newArrayList();

                for (MobEffectInstance effect : potion.getEffects()) {
                    effects.add(new MobEffectInstance(effect.getEffect(), effect.mapDuration(i -> i / 4), effect.getAmplifier(), effect.isAmbient(), effect.isVisible()));
                }

                effects.addAll(this.effects);
                if (effects.isEmpty()) {
                    victims.clear();
                } else {
                    List<LivingEntity> entities = level().getEntitiesOfClass(LivingEntity.class, getBoundingBox());
                    if (!entities.isEmpty()) {
                        for (LivingEntity entity : entities) {
                            if (!victims.containsKey(entity) && entity.isAffectedByPotions()) {
                                if (ownerImmune && getOwner() == entity) continue;
                                victims.put(entity, tickCount + reapplicationDelay);

                                for (MobEffectInstance effect : effects) {
                                    if (effect.getEffect().isInstantenous()) {
                                        effect.getEffect().applyInstantenousEffect(this, getOwner(), entity, effect.getAmplifier(), 0.5D);
                                    } else {
                                        entity.addEffect(new MobEffectInstance(effect), this);
                                    }
                                }

                                if (radiusOnUse != 0.0F) {
                                    radius += radiusOnUse;
                                    if (radius < 0.5F) {
                                        discard();
                                        return;
                                    }

                                    setRadius(radius);
                                }

                                if (durationOnUse != 0) {
                                    duration += durationOnUse;
                                    if (duration <= 0) {
                                        discard();
                                        return;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public void setOwnerImmune(boolean ownerImmune) {
        this.ownerImmune = ownerImmune;
    }

    public float getHeight() {
        return getEntityData().get(DATA_HEIGHT);
    }

    public void setHeight(float height) {
        getEntityData().set(DATA_HEIGHT, height);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        setHeight(pCompound.getFloat("Height"));
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        pCompound.putFloat("Height", getHeight());
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> pKey) {
        if (DATA_HEIGHT.equals(pKey)) {
            refreshDimensions();
        }
        super.onSyncedDataUpdated(pKey);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        getEntityData().define(DATA_HEIGHT, 0.5F);
    }

    @Override
    public EntityDimensions getDimensions(Pose pPose) {
        return EntityDimensions.scalable(getRadius() * 2.0F, getHeight());
    }
}
