package com.bonker.swordinthestone.server.capability;

import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.AutoRegisterCapability;
import net.minecraftforge.common.util.INBTSerializable;

@AutoRegisterCapability
public interface IExtraJumpsCapability extends INBTSerializable<CompoundTag> {
    int extraJumpsUsed();

    void resetExtraJumps();

    void useExtraJump();
}
