package com.bonker.swordinthestone.common.block.blockentity;

import com.bonker.swordinthestone.SwordInTheStone;
import com.bonker.swordinthestone.common.block.SSBlocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class SSBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, SwordInTheStone.MODID);

    public static final RegistryObject<BlockEntityType<SwordStoneBlockEntity>> SWORD_STONE = register("sword_stone",
            () -> BlockEntityType.Builder.of(SwordStoneBlockEntity::new, SSBlocks.SWORD_STONE.get()));

    @SuppressWarnings("DataFlowIssue") // suppress passing null for the unused datatype parameter
    private static <T extends BlockEntity> RegistryObject<BlockEntityType<T>> register(String pKey, Supplier<BlockEntityType.Builder<T>> builderSupplier) {
        return BLOCK_ENTITIES.register(pKey, () -> builderSupplier.get().build(null));
    }
}
