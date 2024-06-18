package com.bonker.swordinthestone.common;

import com.bonker.swordinthestone.SwordInTheStone;
import com.bonker.swordinthestone.util.Util;
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
    public static final RegistryObject<SoundEvent> ROCK = register("rock");
    public static final RegistryObject<SoundEvent> SWORD_PULL = register("sword_pull");
    public static final RegistryObject<SoundEvent> SUCCESS = register("success");
    public static final RegistryObject<SoundEvent> LASER = register("laser");
    public static final RegistryObject<SoundEvent> FIREBALL = register("fireball");
    public static final RegistryObject<SoundEvent> RIFT = register("rift");
    public static final RegistryObject<SoundEvent> JUMP = register("jump");
    public static final RegistryObject<SoundEvent> LAND = register("land");



    private static RegistryObject<SoundEvent> register(String name) {
        return SOUND_EVENTS.register(name, () -> SoundEvent.createVariableRangeEvent(Util.makeResource(name)));
    }
}
