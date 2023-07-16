package com.bonker.swordinthestone.datagen;

import com.bonker.swordinthestone.common.ability.SwordAbilities;
import com.bonker.swordinthestone.common.ability.SwordAbility;
import com.bonker.swordinthestone.common.entity.SSEntityTypes;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.data.LanguageProvider;

public class SSLanguageProvider extends LanguageProvider {
    public SSLanguageProvider(PackOutput output, String modid, String locale) {
        super(output, modid, locale);
    }

    @Override
    protected void addTranslations() {
        add("item_group.swordinthestone.swords", "Unique Swords");
        add("attribute.swordinthestone.extra_jumps", "Jump(s)");

        addEntityType(SSEntityTypes.HEIGHT_AREA_EFFECT_CLOUD, "Area Effect Cloud");
        addEntityType(SSEntityTypes.ENDER_RIFT, "Ender Rift");
        addEntityType(SSEntityTypes.SPELL_FIREBALL, "Fireball");

        add(SwordAbilities.THUNDER_SMITE.get(), "Thunder Smite", "Thundering %1$s", "Sword charges when attacking mobs\nStrikes mobs with lightning when fully charged");
        add(SwordAbilities.VAMPIRIC.get(), "Life Steal", "Vampiric %1$s", "Gain 0-2 hearts upon killing a mob");
        add(SwordAbilities.TOXIC_DASH.get(), "Toxic Dash", "Toxic %1$s", "Slash forward on right click leaving behind a poisonous cloud");
        add(SwordAbilities.ENDER_RIFT.get(), "Ender Rift", "Unstable %1$s", "Hold right click and move your mouse to move an orb\nRelease to teleport to the orb");
        add(SwordAbilities.FIREBALL.get(), "Fireball", "Flaming %1$s", "Hold right click to charge up a fireball attack");
        add(SwordAbilities.DOUBLE_JUMP.get(), "Double Jump", "Aetherial %1$s", "While holding, tap space in the air to double jump\nFall damage is reduced while holding");
        add(SwordAbilities.ALCHEMIST.get(), "Alchemist", "Chemical %1$s", "Inflicts a negative status effect on hit occasionally\nGrants the user a positive status effect on kill occasionally");
        add(SwordAbilities.BAT_SWARM.get(), "Bat Swarm", "Unholy %1$s", "Right click to ride a swarm of bats that attacks mobs");

        add("ability.swordinthestone.alchemist.victim", "Alchemist: Inflicted victim with %1$s");
        add("ability.swordinthestone.alchemist.self", "Alchemist: Applied %1$s to self");
        add("ability.swordinthestone.bat_swarm.name", "%1$s's %2$s");
    }

    private void add(SwordAbility key, String name, String title, String description) {
        ResourceLocation loc = SwordAbilities.SWORD_ABILITY_REGISTRY.get().getKey(key);
        if (loc != null) {
            add("ability." + loc.getNamespace() + "." + loc.getPath(), name);
            add("ability." + loc.getNamespace() + "." + loc.getPath() + ".title", title);
            add("ability." + loc.getNamespace() + "." + loc.getPath() + ".description", description);
        }
    }
}