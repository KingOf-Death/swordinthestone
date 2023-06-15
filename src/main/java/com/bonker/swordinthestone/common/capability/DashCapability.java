package com.bonker.swordinthestone.common.capability;

import com.bonker.swordinthestone.SwordInTheStone;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.AutoRegisterCapability;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;

import java.util.ArrayList;

@AutoRegisterCapability
public class DashCapability {
    public static final Capability<IDashCapability> DASH_DATA = CapabilityManager.get(new CapabilityToken<>(){});

    public static final ResourceLocation NAME = new ResourceLocation(SwordInTheStone.MODID, "dash");

    public static DashCapabilityImplementation create() {
        return new DashCapabilityImplementation();
    }

    public static int getTicks(Player entity) {
        return entity.getCapability(DASH_DATA).resolve().map(IDashCapability::getDashTicks).orElse(0);
    }

    public static void setTicks(Player entity, int dashTicks) {
        entity.getCapability(DASH_DATA).resolve().ifPresent(cap -> cap.setDashTicks(dashTicks));
    }

    private static class DashCapabilityImplementation implements IDashCapability {
        private int dashTicks = 0;
        private final ArrayList<Entity> dashedEntities = new ArrayList<>();

        private DashCapabilityImplementation() {}

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
}
