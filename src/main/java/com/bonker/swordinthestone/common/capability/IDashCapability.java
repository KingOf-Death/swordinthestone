package com.bonker.swordinthestone.common.capability;

import net.minecraft.world.entity.Entity;
import net.minecraftforge.common.capabilities.AutoRegisterCapability;

@AutoRegisterCapability
public interface IDashCapability {
    int getDashTicks();

    void setDashTicks(int dashTicks);

    void addToDashed(Entity entity);

    boolean isDashed(Entity entity);

    void clearDashed();
}
