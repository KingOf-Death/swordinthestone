package com.bonker.swordinthestone.common.networking;

import com.bonker.swordinthestone.SwordInTheStone;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

public class SSNetworking {
    private static SimpleChannel INSTANCE;

    private static int packetId = 0;
    private static int id() {
        return packetId++;
    }

    public static void register() {
        INSTANCE = NetworkRegistry.ChannelBuilder
                .named(new ResourceLocation(SwordInTheStone.MODID, "packets"))
                .networkProtocolVersion(() -> "1.0")
                .clientAcceptedVersions(s -> true)
                .serverAcceptedVersions(s -> true)
                .simpleChannel();

        /* serverbound */

        INSTANCE.messageBuilder(ServerboundDashAttackPacket.class, id(), NetworkDirection.PLAY_TO_SERVER)
                .decoder(ServerboundDashAttackPacket::new)
                .encoder(ServerboundDashAttackPacket::encode)
                .consumerMainThread(ServerboundDashAttackPacket::handle)
                .add();

        INSTANCE.messageBuilder(ServerboundEnderRiftPacket.class, id(), NetworkDirection.PLAY_TO_SERVER)
                .decoder(ServerboundEnderRiftPacket::new)
                .encoder(ServerboundEnderRiftPacket::encode)
                .consumerMainThread(ServerboundEnderRiftPacket::handle)
                .add();

        INSTANCE.messageBuilder(ServerboundExtraJumpPacket.class, id(), NetworkDirection.PLAY_TO_SERVER)
                .decoder(ServerboundExtraJumpPacket::new)
                .encoder(ServerboundExtraJumpPacket::encode)
                .consumerMainThread(ServerboundExtraJumpPacket::handle)
                .add();

        /* clientbound */

        INSTANCE.messageBuilder(ClientboundSyncDeltaPacket.class, id(), NetworkDirection.PLAY_TO_CLIENT)
                .decoder(ClientboundSyncDeltaPacket::new)
                .encoder(ClientboundSyncDeltaPacket::encode)
                .consumerMainThread((packet, contextSupplier) -> packet.handle())
                .add();

        INSTANCE.messageBuilder(ClientboundSyncSwordStoneItemPacket.class, id(), NetworkDirection.PLAY_TO_CLIENT)
                .decoder(ClientboundSyncSwordStoneItemPacket::new)
                .encoder(ClientboundSyncSwordStoneItemPacket::encode)
                .consumerMainThread((packet, contextSupplier) -> packet.handle())
                .add();

        INSTANCE.messageBuilder(ClientboundSyncSwordStoneDataPacket.class, id(), NetworkDirection.PLAY_TO_CLIENT)
                .decoder(ClientboundSyncSwordStoneDataPacket::new)
                .encoder(ClientboundSyncSwordStoneDataPacket::encode)
                .consumerMainThread((packet, contextSupplier) -> packet.handle())
                .add();

        INSTANCE.messageBuilder(ClientboundEnderRiftPacket.class, id(), NetworkDirection.PLAY_TO_CLIENT)
                .decoder(ClientboundEnderRiftPacket::new)
                .encoder(ClientboundEnderRiftPacket::encode)
                .consumerMainThread((packet, contextSupplier) -> packet.handle())
                .add();
    }

    public static <MSG> void sendToServer(MSG message) {
        INSTANCE.sendToServer(message);
    }

    public static <MSG> void sendToPlayer(MSG message, ServerPlayer player) {
        INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), message);
    }

    public static <MSG> void sendToClientsLoadingBE(MSG message, BlockEntity entity) {
        if (entity.getLevel() == null) return;
        INSTANCE.send(PacketDistributor.TRACKING_CHUNK.with(() -> entity.getLevel().getChunkAt(entity.getBlockPos())), message);
    }

    public static <MSG> void sendToTrackingClients(MSG message, Entity entity) {
        INSTANCE.send(PacketDistributor.TRACKING_ENTITY.with(() -> entity), message);
    }
}
