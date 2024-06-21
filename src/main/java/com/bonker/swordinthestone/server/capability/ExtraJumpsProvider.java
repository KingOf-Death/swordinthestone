package com.bonker.swordinthestone.server.capability;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ExtraJumpsProvider implements ICapabilitySerializable<CompoundTag> {
    private final IExtraJumpsCapability cap = new ExtraJumpsCapability();
    private final LazyOptional<IExtraJumpsCapability> optional = LazyOptional.of(() -> cap);

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        return cap == ExtraJumpsCapability.JUMPS ? optional.cast() : LazyOptional.empty();
    }

    @Override
    public CompoundTag serializeNBT() {
        return cap.serializeNBT();
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        cap.deserializeNBT(nbt);
    }
}
