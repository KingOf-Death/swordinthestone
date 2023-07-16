package com.bonker.swordinthestone.common.networking;

import com.bonker.swordinthestone.common.block.entity.SSBlockEntities;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;

public class ClientboundSyncSwordStoneDataPacket {
    private final BlockPos pos;
    private final boolean isProgress; // true - progress; false - idleTicks
    private final short value;

    public ClientboundSyncSwordStoneDataPacket(BlockPos pos, boolean isProgress, short value) {
        this.pos = pos;
        this.isProgress = isProgress;
        this.value = value;
    }

    ClientboundSyncSwordStoneDataPacket(FriendlyByteBuf buf) {
        int x = buf.readVarInt();
        int y = buf.readVarInt();
        int z = buf.readVarInt();
        this.pos = new BlockPos(x, y, z);
        isProgress = buf.readBoolean();
        value = buf.readShort();
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeVarInt(pos.getX());
        buf.writeVarInt(pos.getY());
        buf.writeVarInt(pos.getZ());
        buf.writeBoolean(isProgress);
        buf.writeShort(value);
    }

    public void handle() {
        ClientLevel level = Minecraft.getInstance().level;
        if (level == null) return;
        level.getBlockEntity(pos, SSBlockEntities.SWORD_STONE_MASTER.get()).ifPresent(entity -> {
            if (isProgress) {
                entity.progress = value;
            } else if (Math.abs(entity.idleTicks - value) > 5) {
                entity.idleTicks = value;
            }
        });
    }
}
