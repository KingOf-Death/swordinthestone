package com.bonker.swordinthestone.util;

import com.bonker.swordinthestone.common.entity.EnderRift;
import com.bonker.swordinthestone.common.networking.SSNetworking;
import com.bonker.swordinthestone.common.networking.ServerboundEnderRiftPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;

public class SideUtil {
    public static void controlEnderRift(EnderRift enderRift, Player player) {
        if (player != Minecraft.getInstance().player) return;

        enderRift.setDeltaMovement(enderRift.calculateDelta(player));
        SSNetworking.sendToServer(new ServerboundEnderRiftPacket(enderRift.getId(), enderRift.position(), enderRift.getDeltaMovement()));

        enderRift.move(MoverType.SELF, enderRift.getDeltaMovement());
    }

    public static long getTimeSinceTick(long tick) {
        if (Minecraft.getInstance().level == null) return 0;
        return Minecraft.getInstance().level.getGameTime() - tick;
    }
}
