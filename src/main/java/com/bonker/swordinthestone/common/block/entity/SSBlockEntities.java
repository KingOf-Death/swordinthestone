package com.bonker.swordinthestone.common.block.entity;

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

    public static final RegistryObject<BlockEntityType<SwordStoneMasterBlockEntity>> SWORD_STONE_MASTER = register("sword_stone_master",
            () -> BlockEntityType.Builder.of(SwordStoneMasterBlockEntity::new, SSBlocks.SWORD_STONE.get()));

    public static final RegistryObject<BlockEntityType<SwordStoneDummyBlockEntity>> SWORD_STONE_DUMMY = register("sword_stone_dummy",
            () -> BlockEntityType.Builder.of(SwordStoneDummyBlockEntity::new, SSBlocks.SWORD_STONE.get()));

    @SuppressWarnings("DataFlowIssue") // suppress passing null for the unused datatype parameter
    private static <T extends BlockEntity> RegistryObject<BlockEntityType<T>> register(String pKey, Supplier<BlockEntityType.Builder<T>> builderSupplier) {
        return BLOCK_ENTITIES.register(pKey, () -> builderSupplier.get().build(null));
    }
}
