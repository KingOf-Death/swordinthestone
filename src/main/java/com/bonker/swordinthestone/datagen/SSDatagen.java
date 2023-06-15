package com.bonker.swordinthestone.datagen;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static com.bonker.swordinthestone.SwordInTheStone.MODID;

@Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class SSDatagen {
    @SubscribeEvent
    public static void gatherData(final GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
        PackOutput packOutput = generator.getPackOutput();

        SSLanguageProvider languageProvider = new SSLanguageProvider(packOutput, MODID, "en_us");
        SSAnimatedTextureProvider animatedTextureProvider = new SSAnimatedTextureProvider(packOutput, MODID);

        generator.addProvider(true, new SSItemModelProvider(packOutput, MODID, existingFileHelper, languageProvider, animatedTextureProvider));
        generator.addProvider(true, new SSSoundProvider(packOutput, MODID, existingFileHelper, languageProvider));
        generator.addProvider(true, languageProvider);
        generator.addProvider(true, animatedTextureProvider);
        generator.addProvider(true, new SSBlockStateProvider(packOutput, MODID, existingFileHelper));
    }
}
