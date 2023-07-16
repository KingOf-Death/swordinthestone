package com.bonker.swordinthestone.common;

import com.bonker.swordinthestone.SwordInTheStone;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class SSAttributes {
    public static final DeferredRegister<Attribute> ATTRIBUTES = DeferredRegister.create(ForgeRegistries.ATTRIBUTES, SwordInTheStone.MODID);

    public static final RegistryObject<Attribute> JUMPS = ATTRIBUTES.register("extra_jumps",
            () -> new RangedAttribute("attribute.swordinthestone.extra_jumps", 0, 0, 64));
}
