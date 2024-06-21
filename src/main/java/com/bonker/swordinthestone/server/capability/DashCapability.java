package com.bonker.swordinthestone.server.capability;

import com.bonker.swordinthestone.util.Util;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.ArrayList;

public class DashCapability implements IDashCapability {
    public static final Capability<IDashCapability> DASH = CapabilityManager.get(new CapabilityToken<>(){});

    public static final ResourceLocation NAME = Util.makeResource("dash");

    public static LazyOptional<IDashCapability> createOptional() {
        IDashCapability cap = new DashCapability();
        return LazyOptional.of(() -> cap);
    }

    public static ICapabilityProvider createProvider() {
        return new ICapabilityProvider() {
            private final LazyOptional<IDashCapability> optional = createOptional();
            @Override
            public <T> @NotNull LazyOptional<T> getCapability(Capability<T> cap, @Nullable Direction side) {
                return DashCapability.DASH.orEmpty(cap, optional);
            }
        };
    }

    public static int getTicks(Player entity) {
        return entity.getCapability(DASH).resolve().map(IDashCapability::getDashTicks).orElse(0);
    }

    public static void setTicks(Player entity, int dashTicks) {
        entity.getCapability(DASH).resolve().ifPresent(cap -> cap.setDashTicks(dashTicks));
    }

    // implementation
    private int dashTicks = 0;
    private final ArrayList<Entity> dashedEntities = new ArrayList<>();

    @Override
    public int getDashTicks() {
        return dashTicks;
    }

    @Override
    public void setDashTicks(int dashTicks) {
        this.dashTicks = dashTicks;
    }

    @Override
    public void addToDashed(Entity entity) {
        dashedEntities.add(entity);
    }

    @Override
    public boolean isDashed(Entity entity) {
        return dashedEntities.contains(entity);
    }

    @Override
    public void clearDashed() {
        dashedEntities.clear();
    }
}
