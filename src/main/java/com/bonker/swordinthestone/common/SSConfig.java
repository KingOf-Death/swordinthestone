package com.bonker.swordinthestone.common;

import com.bonker.swordinthestone.SwordInTheStone;
import com.bonker.swordinthestone.common.item.UniqueSwordItem;
import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;
import com.mojang.logging.LogUtils;
import net.minecraft.ResourceLocationException;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import org.slf4j.Logger;

import java.nio.file.Path;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

@Mod.EventBusSubscriber(modid = SwordInTheStone.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class SSConfig {
    private static final Logger LOGGER = LogUtils.getLogger();

    private static final ForgeConfigSpec.Builder COMMON_BUILDER = new ForgeConfigSpec.Builder();
    public static ForgeConfigSpec COMMON_CONFIG;

    private static final ForgeConfigSpec.ConfigValue<List<? extends String>> DISABLED_ABILITIES;
    public static Collection<ResourceLocation> disabledAbilities = List.of();

    static {
        List<String> empty = Collections.emptyList();
        Predicate<Object> validator = o -> o instanceof String && ((String) o).contains(":");

        DISABLED_ABILITIES = COMMON_BUILDER
                .comment("Add the ids of sword abilities here to disabled them. Ex. \"swordinthestone:thunder_smite\"" +
                        "\nPlease note that you will not receive an error if you mis-type these values. That ability will just not be disabled")
                .translation("swordinthestone.configgui.disabledAbilities")
                .defineList("disabledAbilities", empty, validator);

        COMMON_CONFIG = COMMON_BUILDER.build();
    }

    public static void load(Path path) {
        CommentedFileConfig configData = CommentedFileConfig.builder(path).sync().autosave().writingMode(WritingMode.REPLACE).build();
        configData.load();
        COMMON_CONFIG.setConfig(configData);
    }

    @SubscribeEvent
    public static void onFileChange(final ModConfigEvent.Reloading event) {
        if (event.getConfig().getType() == ModConfig.Type.COMMON) {
            try {
                disabledAbilities = DISABLED_ABILITIES.get().stream().map(ResourceLocation::tryParse).toList();
                UniqueSwordItem.reloadAbilities();
            } catch (ResourceLocationException e) {
                disabledAbilities = List.of();
                LOGGER.error("Error loading swordinthestone common config", e);
            }
        }
    }
}
