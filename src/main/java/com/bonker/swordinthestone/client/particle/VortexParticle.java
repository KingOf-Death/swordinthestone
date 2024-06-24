package com.bonker.swordinthestone.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.SimpleParticleType;

public class VortexParticle extends TextureSheetParticle {
    private final SpriteSet sprites;
    private final float rollSpeed;

    protected VortexParticle(ClientLevel pLevel, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed, SpriteSet pSpriteSet) {
        super(pLevel, pX, pY, pZ, pXSpeed, pYSpeed, pZSpeed);

        this.lifetime = 45 + pLevel.random.nextInt(15);

        this.sprites = pSpriteSet;
        setSpriteFromAge(sprites);

        this.xd = pXSpeed * 0.4;
        this.yd = pYSpeed * 0.4;
        this.zd = pZSpeed * 0.4;

        this.rollSpeed = (pLevel.random.nextFloat() - 0.5F) * 0.1F;
    }

    @Override
    public void tick() {
        super.tick();
        setSpriteFromAge(sprites);

        oRoll = roll;
        roll += rollSpeed;

        scale(0.96F);
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_LIT;
    }

    @Override
    public int getLightColor(float pPartialTick) {
        BlockPos pos = BlockPos.containing(this.x, this.y, this.z);
        return level.hasChunkAt(pos) ? LevelRenderer.getLightColor(level, pos) : 0;
    }

    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;

        public Provider(SpriteSet pSprite) {
            this.sprites = pSprite;
        }

        public Particle createParticle(SimpleParticleType pType, ClientLevel pLevel, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed) {
            return new VortexParticle(pLevel, pX, pY, pZ, pXSpeed, pYSpeed, pZSpeed, sprites);
        }
    }
}
