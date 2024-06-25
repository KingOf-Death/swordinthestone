package com.bonker.swordinthestone.server.capability;

import com.bonker.swordinthestone.common.SSAttributes;
import com.bonker.swordinthestone.util.Util;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;

import java.util.Optional;

public class ExtraJumpsCapability implements IExtraJumpsCapability {
    public static final Capability<IExtraJumpsCapability> JUMPS = CapabilityManager.get(new CapabilityToken<>(){});
    public static final ResourceLocation NAME = Util.makeResource("jumps");
    private static final String EXTRA_JUMPS_KEY = "extraJumps";

    public static int getJumpsUsed(Player player) {
        Optional<IExtraJumpsCapability> optional = player.getCapability(JUMPS).resolve();
        return optional.map(IExtraJumpsCapability::extraJumpsUsed).orElse(0);
    }

    public static boolean hasExtraJump(Player player) {
        return player.getAttributeValue(SSAttributes.JUMPS.get()) - getJumpsUsed(player) > 0;
    }

    public static void useJump(Player player) {
        player.getCapability(JUMPS).ifPresent(IExtraJumpsCapability::useExtraJump);
    }

    // implementation
    private int extraJumps = 0;

    @Override
    public int extraJumpsUsed() {
        return extraJumps;
    }

    @Override
    public void resetExtraJumps() {
        extraJumps = 0;
    }

    @Override
    public void useExtraJump() {
        extraJumps++;
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();
        nbt.putInt(EXTRA_JUMPS_KEY, extraJumps);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        extraJumps = nbt.getInt(EXTRA_JUMPS_KEY);
    }

    ExtraJumpsCapability() {}
}
