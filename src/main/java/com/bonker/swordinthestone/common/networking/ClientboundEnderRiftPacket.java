package com.bonker.swordinthestone.common.networking;

import com.bonker.swordinthestone.common.entity.SSEntityTypes;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.Vec3;

public class ClientboundEnderRiftPacket {
    protected final int entityId;
    protected final Vec3 position, delta;

    public ClientboundEnderRiftPacket(int entityId, Vec3 position, Vec3 delta) {
        this.entityId = entityId;
        this.position = position;
        this.delta = delta;
    }

    ClientboundEnderRiftPacket(FriendlyByteBuf buf) {
        entityId = buf.readVarInt();
        position = new Vec3(buf.readDouble(), buf.readDouble(), buf.readDouble());
        delta = new Vec3(buf.readDouble(), buf.readDouble(), buf.readDouble());
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeVarInt(entityId);
        buf.writeDouble(position.x());
        buf.writeDouble(position.y());
        buf.writeDouble(position.z());
        buf.writeDouble(delta.x());
        buf.writeDouble(delta.y());
        buf.writeDouble(delta.z());
    }

    public void handle() {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) return;
        ClientLevel level = (ClientLevel) player.level();

        Entity entity = level.getEntity(entityId);
        if (entity == null || entity.getType() != SSEntityTypes.ENDER_RIFT.get()) return;
        if (((Projectile) entity).getOwner() == player) return;

        entity.setPos(position);
        entity.setDeltaMovement(delta);
    }
}
