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
        add("item_group.swordinthestone.swords", "Sword in the Stone");
        add("attribute.swordinthestone.extra_jumps", "Jump(s)");

        addEntityType(SSEntityTypes.HEIGHT_AREA_EFFECT_CLOUD, "Area Effect Cloud");
        addEntityType(SSEntityTypes.ENDER_RIFT, "Ender Rift");
        addEntityType(SSEntityTypes.SPELL_FIREBALL, "Fireball");

        add(SwordAbilities.THUNDER_SMITE.get(), "Thunder Smite", "Thundering %s", "Sword charges when attacking mobs\nStrikes mobs with lightning when fully charged");
        add(SwordAbilities.VAMPIRIC.get(), "Life Steal", "Vampiric %s", "Steal some of an enemy's health upon killing it");
        add(SwordAbilities.TOXIC_DASH.get(), "Toxic Dash", "Toxic %s", "Slash forward on right click leaving behind a poisonous cloud");
        add(SwordAbilities.ENDER_RIFT.get(), "Ender Rift", "Unstable %s", "Hold right click and move your mouse to move an orb\nRelease to teleport to the orb");
        add(SwordAbilities.FIREBALL.get(), "Fireball", "Flaming %s", "Hold right click to charge up a fireball attack");
        add(SwordAbilities.DOUBLE_JUMP.get(), "Double Jump", "Aetherial %s", "While holding, tap space in the air to double jump\nFall damage is reduced while holding");
        add(SwordAbilities.ALCHEMIST.get(), "Alchemist", "Chemical %s", "Inflicts a negative status effect on hit occasionally\nGrants the user a positive status effect on kill occasionally");
        add(SwordAbilities.BAT_SWARM.get(), "Bat Swarm", "Unholy %s", "Right click to ride a swarm of bats that attacks mobs");

        add("ability.swordinthestone.alchemist.victim", "Alchemist: Inflicted victim with %s");
        add("ability.swordinthestone.alchemist.self", "Alchemist: Applied %s to self");
        add("ability.swordinthestone.alchemist.potion", "%s (%ds)");
        add("ability.swordinthestone.alchemist.potionAmplifier", "%s %s (%ds)");
        add("ability.swordinthestone.bat_swarm.name", "%s's %s");

        // sword stats
        addConfig("baseDamage", "Base Sword Damage");
        addConfig("maxDamageModifier", "Max Sword Damage Modifier");
        addConfig("baseAttackSpeed", "Base Sword Attack Speed");
        addConfig("maxAttackSpeedModifier", "Max Attack Speed Modifier");
        addConfig("durability", "Sword Durability");
        // sword stone
        addConfig("swordBeaconEnabled", "Enable Sword Stone Periodic Beacon Beam");
        addConfig("disabledAbilities", "Disabled Sword Abilities");
        addConfig("swordStoneSpacingOverworld", "Overworld Sword Stone Generation Spacing");
        addConfig("swordStoneSeparationOverworld", "Overworld Sword Stone Generation Separation");
        addConfig("swordStoneSpacingEnd", "End Sword Stone Generation Spacing");
        addConfig("swordStoneSeparationEnd", "End Sword Stone Generation Separation");
        addConfig("swordStoneSpacingNether", "Nether Sword Stone Generation Spacing");
        addConfig("swordStoneSeparationNether", "Nether Sword Stone Generation Separation");
        // abilities
        addConfig("thunderSmiteCharges", "Thunder Smite Charges");
        addConfig("vampiricHealthPercent", "Vampiric Life Steal Percent");
        addConfig("vampiricHealthCap", "Vampiric Life Steal Cap");
        addConfig("toxicDashCooldown", "Toxic Dash Cooldown");
        addConfig("enderRiftCooldown", "Ender Rift Cooldown");
        addConfig("enderRiftDuration", "Ender Rift Projectile Duration");
        addConfig("fireballCooldown", "Fireball Cooldown");
        addConfig("fireballDestroyBlocks", "Ability Fireballs Destroy Blocks");
        addConfig("fireballSetFire", "Ability Fireballs Create Fire");
        addConfig("fireballMaxPower", "Max Fireball Explosion Power");
        addConfig("fireballChargeRate", "Fireball Power Charge Rate");
        addConfig("doubleJumpVehicle", "Double Jump While Riding");
        addConfig("alchemistSelfChance", "Alchemist Splash Self Chance");
        addConfig("alchemistVictimChance", "Alchemist Splash Victim Chance");
        addConfig("batSwarmCooldown", "Bat Swarm Cooldown");
        addConfig("batSwarmDuration", "Bat Swarm Duration");
        addConfig("batSwarmDamage", "Bat Swarm Attack Damage");
    }

    private void add(SwordAbility key, String name, String title, String description) {
        ResourceLocation loc = SwordAbilities.SWORD_ABILITY_REGISTRY.get().getKey(key);
        if (loc != null) {
            add("ability." + loc.getNamespace() + "." + loc.getPath(), name);
            add("ability." + loc.getNamespace() + "." + loc.getPath() + ".title", title);
            add("ability." + loc.getNamespace() + "." + loc.getPath() + ".description", description);
        }
    }

    private void addConfig(String path, String name) {
        add("swordinthestone.configgui." + path, name);
    }
}