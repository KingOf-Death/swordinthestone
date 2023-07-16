package com.bonker.swordinthestone.client;

import com.bonker.swordinthestone.SwordInTheStone;
import com.bonker.swordinthestone.client.particle.AirParticle;
import com.bonker.swordinthestone.client.particle.FireParticle;
import com.bonker.swordinthestone.client.particle.HealParticle;
import com.bonker.swordinthestone.client.particle.SSParticles;
import com.bonker.swordinthestone.client.renderer.SSBEWLR;
import com.bonker.swordinthestone.client.renderer.SwordStoneBlockEntityRenderer;
import com.bonker.swordinthestone.client.renderer.entity.EnderRiftRenderer;
import com.bonker.swordinthestone.client.renderer.entity.SpellFireballRenderer;
import com.bonker.swordinthestone.common.ability.SwordAbilities;
import com.bonker.swordinthestone.common.block.entity.SSBlockEntities;
import com.bonker.swordinthestone.common.entity.SSEntityTypes;
import com.bonker.swordinthestone.common.item.SSItems;
import com.bonker.swordinthestone.common.item.UniqueSwordItem;
import com.bonker.swordinthestone.common.networking.SSNetworking;
import com.bonker.swordinthestone.common.networking.ServerboundExtraJumpPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.entity.NoopRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.lwjgl.glfw.GLFW;

public class ClientEvents {
    private static Minecraft minecraft;

    @Mod.EventBusSubscriber(modid = SwordInTheStone.MODID, value = Dist.CLIENT)
    public static class ForgeBus {
        @SubscribeEvent
        public static void onKeyInput(final InputEvent.Key event) {
            LocalPlayer player = Minecraft.getInstance().player;
            if (player != null &&
                    event.getAction() == GLFW.GLFW_PRESS &&
                    event.getKey() == minecraft.options.keyJump.getKey().getValue() &&
                    !player.onGround() && !player.isFallFlying()) {
                SSNetworking.sendToServer(new ServerboundExtraJumpPacket(player.input.left, player.input.right, player.input.up, player.input.down));
            }
        }
    }

    @Mod.EventBusSubscriber(modid = SwordInTheStone.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ModBus {
        @SubscribeEvent
        public static void onClientSetup(final FMLClientSetupEvent event) {
            minecraft = Minecraft.getInstance();
            SSBEWLR.INSTANCE = new SSBEWLR(minecraft.getBlockEntityRenderDispatcher(), minecraft.getEntityModels());
        }

        @SubscribeEvent
        public static void onRegisterEntityRenderers(final EntityRenderersEvent.RegisterRenderers event) {
            event.registerEntityRenderer(SSEntityTypes.HEIGHT_AREA_EFFECT_CLOUD.get(), NoopRenderer::new);
            event.registerEntityRenderer(SSEntityTypes.ENDER_RIFT.get(), EnderRiftRenderer::new);
            event.registerEntityRenderer(SSEntityTypes.SPELL_FIREBALL.get(), SpellFireballRenderer::new);

            event.registerBlockEntityRenderer(SSBlockEntities.SWORD_STONE_MASTER.get(), SwordStoneBlockEntityRenderer::new);
        }

        @SubscribeEvent
        public static void onRegisterAdditionalModels(final ModelEvent.RegisterAdditional event) {
            for (ResourceLocation ability : SwordAbilities.SWORD_ABILITY_REGISTRY.get().getKeys()) {
                event.register(new ResourceLocation(ability.getNamespace(), "item/ability/" + ability.getPath()));
            }
            for (RegistryObject<Item> regObj : SSItems.ITEMS.getEntries()) {
                if (!(regObj.get() instanceof UniqueSwordItem)) continue;
                ResourceLocation loc = ForgeRegistries.ITEMS.getKey(regObj.get());
                if (loc == null) continue;
                event.register(new ResourceLocation(loc.getNamespace(), "item/sword/" + loc.getPath()));
            }
        }

        @SubscribeEvent
        public static void onRegisterParticleProviders(final RegisterParticleProvidersEvent event) {
            event.registerSpriteSet(SSParticles.HEAL.get(), HealParticle.Provider::new);
            event.registerSpriteSet(SSParticles.FIRE.get(), FireParticle.Provider::new);
            event.registerSpriteSet(SSParticles.AIR.get(), AirParticle.Provider::new);
        }
    }
}
