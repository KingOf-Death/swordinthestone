package com.bonker.swordinthestone.server.capability;

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
