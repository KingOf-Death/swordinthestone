package com.bonker.swordinthestone.common;

import com.bonker.swordinthestone.SwordInTheStone;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class SSSounds {
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, SwordInTheStone.MODID);

    public static final RegistryObject<SoundEvent> ZAP = register("zap");
    public static final RegistryObject<SoundEvent> HEAL = register("heal");
    public static final RegistryObject<SoundEvent> TOXIC = register("toxic");
    public static final RegistryObject<SoundEvent> DASH = register("dash");


    private static RegistryObject<SoundEvent> register(String name) {
        return SOUND_EVENTS.register(name, () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(SwordInTheStone.MODID, name)));
    }
}
