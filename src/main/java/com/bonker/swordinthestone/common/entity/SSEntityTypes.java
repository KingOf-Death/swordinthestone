package com.bonker.swordinthestone.common.entity;

import com.bonker.swordinthestone.SwordInTheStone;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class SSEntityTypes {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, SwordInTheStone.MODID);


    public static final RegistryObject<EntityType<HeightAreaEffectCloud>> HEIGHT_AREA_EFFECT_CLOUD = register("height_area_effect_cloud",
            EntityType.Builder.<HeightAreaEffectCloud>of(HeightAreaEffectCloud::new, MobCategory.MISC)
                    .fireImmune()
                    .sized(6.0F, 0.5F)
                    .clientTrackingRange(10)
                    .updateInterval(Integer.MAX_VALUE));

    private static <T extends Entity> RegistryObject<EntityType<T>> register(String key, EntityType.Builder<T> builder) {
        return ENTITY_TYPES.register(key, () -> builder.build(key));
    }
}
