package com.bonker.swordinthestone.client.particle;

import com.bonker.swordinthestone.SwordInTheStone;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class SSParticles {
    public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, SwordInTheStone.MODID);

    public static final RegistryObject<SimpleParticleType> HEAL = PARTICLE_TYPES.register("heal",
            () -> new SimpleParticleType(false));
}
