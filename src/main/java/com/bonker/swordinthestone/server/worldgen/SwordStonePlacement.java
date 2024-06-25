package com.bonker.swordinthestone.server.worldgen;

import com.bonker.swordinthestone.common.SSConfig;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadStructurePlacement;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadType;

import java.util.function.Supplier;

public class SwordStonePlacement extends RandomSpreadStructurePlacement {
    public static final Codec<SwordStonePlacement> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            RandomSpreadType.CODEC.optionalFieldOf("spread_type", RandomSpreadType.LINEAR).forGetter(RandomSpreadStructurePlacement::spreadType),
            Codec.INT.fieldOf("salt").forGetter(p -> p.salt()),
            SwordStoneType.CODEC.fieldOf("sword_stone_type").forGetter(p -> p.swordStoneType)
    ).apply(inst, SwordStonePlacement::new));

    private final SwordStoneType swordStoneType;

    private SwordStonePlacement(RandomSpreadType pSpreadType, int pSalt, SwordStoneType swordStoneType) {
        super(swordStoneType.spacingSupplier.get(), swordStoneType.separationSupplier.get(), pSpreadType, pSalt);
        this.swordStoneType = swordStoneType;
    }

    private enum SwordStoneType implements StringRepresentable {
        OVERWORLD("overworld", SSConfig.SWORD_STONE_SPACING_OVERWORLD, SSConfig.SWORD_STONE_SEPARATION_OVERWORLD),
        END("end", SSConfig.SWORD_STONE_SPACING_END, SSConfig.SWORD_STONE_SEPARATION_END),
        NETHER("nether", SSConfig.SWORD_STONE_SPACING_NETHER, SSConfig.SWORD_STONE_SEPARATION_NETHER);

        private static final Codec<SwordStoneType> CODEC = StringRepresentable.fromEnum(SwordStoneType::values);

        private final Supplier<Integer> spacingSupplier, separationSupplier;
        private final String name;

        SwordStoneType(String name, Supplier<Integer> spacingSupplier, Supplier<Integer> separationSupplier) {
            this.spacingSupplier = spacingSupplier;
            this.separationSupplier = separationSupplier;
            this.name = name;
        }

        @Override
        public String getSerializedName() {
            return name;
        }
    }
}
