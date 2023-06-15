package com.bonker.swordinthestone.common.networking;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.phys.Vec3;

public class ClientboundSyncDeltaPacket {
    private final double xd, yd, zd;

    public ClientboundSyncDeltaPacket(Vec3 delta) {
        this.xd = delta.x;
        this.yd = delta.y;
        this.zd = delta.z;
    }

    ClientboundSyncDeltaPacket(FriendlyByteBuf buf) {
        this.xd = buf.readDouble();
        this.yd = buf.readDouble();
        this.zd = buf.readDouble();
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeDouble(xd);
        buf.writeDouble(yd);
        buf.writeDouble(zd);
    }

    public void handle() {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player != null) player.setDeltaMovement(new Vec3(xd, yd, zd));
    }
}
