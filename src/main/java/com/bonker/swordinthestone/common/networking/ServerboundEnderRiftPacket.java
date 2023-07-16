package com.bonker.swordinthestone.common.networking;

import com.bonker.swordinthestone.common.entity.SSEntityTypes;
import com.mojang.logging.LogUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkEvent;
import org.slf4j.Logger;

import java.util.function.Supplier;

public class ServerboundEnderRiftPacket extends ClientboundEnderRiftPacket {
    private static final Logger LOGGER = LogUtils.getLogger();

    public ServerboundEnderRiftPacket(int entityId, Vec3 position, Vec3 delta) {
        super(entityId, position, delta);
    }

    ServerboundEnderRiftPacket(FriendlyByteBuf buf) {
        super(buf);
    }

    public void handle(Supplier<NetworkEvent.Context> contextSupplier) {
        ServerPlayer player = contextSupplier.get().getSender();
        if (player == null) return;
        ServerLevel level = player.serverLevel();

        Entity entity = level.getEntity(entityId);
        if (entity == null || entity.getType() != SSEntityTypes.ENDER_RIFT.get()) return;
        if (((Projectile) entity).getOwner() != player) return;

        if (entity.position().distanceToSqr(position) < 25) {
            entity.setPos(position);
            entity.setDeltaMovement(delta);

            SSNetworking.sendToTrackingClients(new ClientboundEnderRiftPacket(entityId, entity.position(), entity.getDeltaMovement()), entity);
        } else {
            LOGGER.warn("{} controlled an ender rift suspiciously", player.getScoreboardName());
        }
    }
}
