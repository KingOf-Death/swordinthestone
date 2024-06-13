package com.bonker.swordinthestone.common.entity;

import com.bonker.swordinthestone.client.ClientUtil;
import com.bonker.swordinthestone.common.networking.ClientboundEnderRiftPacket;
import com.bonker.swordinthestone.common.networking.SSNetworking;
import com.bonker.swordinthestone.util.Util;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashSet;

public class EnderRift extends Projectile {
    public static final EntityDataAccessor<Boolean> DATA_CONTROLLING = SynchedEntityData.defineId(EnderRift.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<Vec3> DATA_ANGLE = SynchedEntityData.defineId(EnderRift.class, Util.VEC3);

    private int age = 0;
    private final LinkedHashSet<Entity> entities = new LinkedHashSet<>();

    public EnderRift(EntityType<? extends Projectile> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public EnderRift(Level level, LivingEntity owner) {
        this(SSEntityTypes.ENDER_RIFT.get(), level);
        setPos(owner.getEyePosition());
        setOwner(owner);
    }

    @Override
    public void tick() {
        super.tick();

        if (++age >= 60) {
            teleport();
        }

        if (level().isClientSide) {
            addParticles(this, 3, 0);
            entities.forEach(e -> addParticles(e, 1, e.getBbHeight()));
        }

        if (!(getOwner() instanceof Player owner)) return;

        if (getEntityData().get(DATA_CONTROLLING)) {
            if (level().isClientSide) ClientUtil.controlEnderRift(this, owner);
        } else {
            move(MoverType.SELF, getDeltaMovement());

            if (age % 5 == 0 && !level().isClientSide)
                SSNetworking.sendToTrackingClients(new ClientboundEnderRiftPacket(getId(), position(), getDeltaMovement()), this);
        }

        if (age % 2 == 0) {
            entities.addAll(level().getEntities(this, getBoundingBox().inflate(0.3)));
        }
    }

    public Vec3 calculateDelta(LivingEntity entity) {
        Vec3 initialAngle = getEntityData().get(DATA_ANGLE);
        Vec3 diff = entity.getLookAngle().subtract(initialAngle);
        diff = diff.scale(0.5);
        return initialAngle.scale(0.2).add(diff);
    }

    public void teleport() {
        if (!entities.isEmpty()) {
            entities.forEach(entity -> {
                entity.teleportTo(Mth.floor(getX()) + 0.5, Mth.floor(getY()), Mth.floor(getZ()) + 0.5);
                entity.resetFallDistance();
            });

            playSound(SoundEvents.CHORUS_FRUIT_TELEPORT);
        }

        discard();
    }

    private static void addParticles(Entity entity, int count, double offset) {
        for (int i = 0; i < count; i++) {
            entity.level().addParticle(ParticleTypes.WITCH, entity.getX(), entity.getY() + offset, entity.getZ(), 0, 0, 0);
        }
    }

    @Override
    public void setOwner(@Nullable Entity pOwner) {
        super.setOwner(pOwner);

        if (pOwner != null) {
            entities.add(pOwner);
            entities.addAll(pOwner.getPassengers());
            Entity vehicle = pOwner.getVehicle();
            if (vehicle != null) entities.add(vehicle);
        }
    }

    @Override
    public boolean shouldRenderAtSqrDistance(double pDistance) {
        return pDistance < 16384; // 128^2
    }

    @Override
    protected void defineSynchedData() {
        getEntityData().define(DATA_CONTROLLING, true);
        getEntityData().define(DATA_ANGLE, Vec3.ZERO);
    }
}
