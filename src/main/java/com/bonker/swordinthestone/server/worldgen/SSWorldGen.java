package com.bonker.swordinthestone.server.worldgen;

import com.bonker.swordinthestone.SwordInTheStone;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.structure.placement.StructurePlacementType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class SSWorldGen {
    public static final DeferredRegister<StructurePlacementType<?>> STRUCTURE_PLACEMENT_TYPES =
            DeferredRegister.create(Registries.STRUCTURE_PLACEMENT, SwordInTheStone.MODID);

    public static final RegistryObject<StructurePlacementType<SwordStonePlacement>> SWORD_STONE_PLACEMENT =
            STRUCTURE_PLACEMENT_TYPES.register("sword_stone_placement", () -> () -> SwordStonePlacement.CODEC);
}
