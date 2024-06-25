package com.bonker.swordinthestone.common.networking;

import com.bonker.swordinthestone.common.entity.HeightAreaEffectCloud;
import com.bonker.swordinthestone.server.capability.DashCapability;
import com.bonker.swordinthestone.server.capability.IDashCapability;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.network.NetworkEvent;

import java.util.Optional;
import java.util.function.Supplier;

public class ServerboundDashAttackPacket {
    private final int entityId;

    public ServerboundDashAttackPacket(int entityId) {
        this.entityId = entityId;
    }

    ServerboundDashAttackPacket(FriendlyByteBuf buf) {
        entityId = buf.readVarInt();
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeVarInt(entityId);
    }

    public void handle(Supplier<NetworkEvent.Context> contextSupplier) {
        ServerPlayer player = contextSupplier.get().getSender();
        if (player == null) return;
        Optional<IDashCapability> cap = player.getCapability(DashCapability.DASH).resolve();
        if (cap.isEmpty() || cap.get().getDashTicks() <= 0) return;
        ServerLevel level = player.serverLevel();
        Entity entity = level.getEntity(entityId);
        if (entity == null) return;
        if (player.distanceTo(entity) > player.getAttributeValue(ForgeMod.ENTITY_REACH.get()) * 2) return;
        player.attackStrengthTicker = 100;
        player.attack(entity);
        player.resetAttackStrengthTicker();
        HeightAreaEffectCloud.createToxicDashCloud(player.level(), player, player.getX(), player.getY() - 0.5, player.getZ());
    }
}
