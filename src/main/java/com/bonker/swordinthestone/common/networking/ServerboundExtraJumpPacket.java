package com.bonker.swordinthestone.common.networking;

import com.bonker.swordinthestone.common.SSConfig;
import com.bonker.swordinthestone.common.capability.ExtraJumpsCapability;
import com.bonker.swordinthestone.util.DoubleJumpEvent;
import com.bonker.swordinthestone.util.Util;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ServerboundExtraJumpPacket {
    private final boolean left, right, forward, backward;

    public ServerboundExtraJumpPacket(boolean left, boolean right, boolean forward, boolean backward) {
        this.left = left;
        this.right = right;
        this.forward = forward;
        this.backward = backward;
    }

    public ServerboundExtraJumpPacket(FriendlyByteBuf buf) {
        left = buf.readBoolean();
        right = buf.readBoolean();
        forward = buf.readBoolean();
        backward = buf.readBoolean();
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeBoolean(left);
        buf.writeBoolean(right);
        buf.writeBoolean(forward);
        buf.writeBoolean(backward);
    }

    public void handle(Supplier<NetworkEvent.Context> contextSupplier) {
        ServerPlayer player = contextSupplier.get().getSender();
        if (player == null) return;
        if (ExtraJumpsCapability.hasExtraJump(player)) {
            if (!MinecraftForge.EVENT_BUS.post(new DoubleJumpEvent(player))) {
                double forwards = 0, sideways = 0;
                if (left) sideways += 0.25;
                if (right) sideways -= 0.25;
                if (forward) forwards += 0.5;
                if (backward) forwards -= 0.5;

                Vec3 delta = Util.relativeVec(new Vec2(0, player.getYRot()), forwards, 0.5, sideways);
                ExtraJumpsCapability.useJump(player);

                if (player.getVehicle() == null || !SSConfig.DOUBLE_JUMP_VEHICLE.get()) {
                    player.setDeltaMovement(delta);
                    SSNetworking.sendToPlayer(new ClientboundSyncDeltaPacket(delta), player);
                } else {
                    player.getVehicle().setDeltaMovement(delta);
                    SSNetworking.sendToPlayer(new ClientboundSyncDeltaPacket(delta, player.getVehicle().getId()), player);
                }
            }
        }
    }
}
