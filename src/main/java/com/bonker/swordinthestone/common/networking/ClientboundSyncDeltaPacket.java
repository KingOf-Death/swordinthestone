package com.bonker.swordinthestone.common.networking;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

public class ClientboundSyncDeltaPacket {
    private final double xd, yd, zd;
    private final int entityId;

    public ClientboundSyncDeltaPacket(Vec3 delta, int entityId) {
        this.xd = delta.x;
        this.yd = delta.y;
        this.zd = delta.z;
        this.entityId = entityId;
    }

    public ClientboundSyncDeltaPacket(Vec3 delta) {
        this(delta, -1);
    }

    ClientboundSyncDeltaPacket(FriendlyByteBuf buf) {
        this.xd = buf.readDouble();
        this.yd = buf.readDouble();
        this.zd = buf.readDouble();
        this.entityId = buf.readVarInt();
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeDouble(xd);
        buf.writeDouble(yd);
        buf.writeDouble(zd);
        buf.writeVarInt(entityId);
    }

    public void handle() {
        if (Minecraft.getInstance().level == null) return;
        if (entityId != -1) {
            Entity entity = Minecraft.getInstance().level.getEntity(entityId);
            if (entity != null) entity.setDeltaMovement(new Vec3(xd, yd, zd));
        }
        if (Minecraft.getInstance().player != null) {
            Minecraft.getInstance().player.setDeltaMovement(new Vec3(xd, yd, zd));
        }
    }
}
