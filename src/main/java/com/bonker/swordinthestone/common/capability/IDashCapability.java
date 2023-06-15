package com.bonker.swordinthestone.common.capability;

import net.minecraft.world.entity.Entity;

public interface IDashCapability {
    int getDashTicks();

    void setDashTicks(int dashTicks);

    void addToDashed(Entity entity);

    boolean isDashed(Entity entity);

    void clearDashed();
}
