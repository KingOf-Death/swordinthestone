package com.bonker.swordinthestone.common.networking;

import com.bonker.swordinthestone.common.block.entity.SSBlockEntities;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;

public class ClientboundSyncSwordStoneItemPacket {
    private final BlockPos pos;
    private final ItemStack stack;

    public ClientboundSyncSwordStoneItemPacket(BlockPos pos, ItemStack stack) {
        this.pos = pos;
        this.stack = stack;
    }

    ClientboundSyncSwordStoneItemPacket(FriendlyByteBuf buf) {
        int x = buf.readVarInt();
        int y = buf.readVarInt();
        int z = buf.readVarInt();
        this.pos = new BlockPos(x, y, z);
        this.stack = buf.readItem();
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeVarInt(pos.getX());
        buf.writeVarInt(pos.getY());
        buf.writeVarInt(pos.getZ());
        buf.writeItem(stack);
    }

    public void handle() {
        ClientLevel level = Minecraft.getInstance().level;
        if (level == null) return;
        level.getBlockEntity(pos, SSBlockEntities.SWORD_STONE.get()).ifPresent(entity -> entity.setItem(stack));
    }
}
