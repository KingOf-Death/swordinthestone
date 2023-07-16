package com.bonker.swordinthestone.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;

public class AirParticle extends TextureSheetParticle {
    private static final float RED = 178 / 255F;
    private static final float GREEN = 220 / 255F;
    private static final float BLUE = 231 / 255F;

    private final SpriteSet sprites;

    public AirParticle(ClientLevel pLevel, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed, SpriteSet pSpriteSet) {
        super(pLevel, pX, pY, pZ, pXSpeed, pYSpeed, pZSpeed);

        this.lifetime = 40 + pLevel.random.nextInt(15);

        this.sprites = pSpriteSet;
        setSpriteFromAge(sprites);

        this.xd = pXSpeed;
        this.yd = -Math.abs(pYSpeed);
        this.zd = pZSpeed;

        this.rCol = RED;
        this.gCol = GREEN;
        this.bCol = BLUE;
    }

    @Override
    public void tick() {
        super.tick();
        setSpriteFromAge(sprites);
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
    }

    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;

        public Provider(SpriteSet pSprites) {
            this.sprites = pSprites;
        }

        public Particle createParticle(SimpleParticleType options, ClientLevel pLevel, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed) {
            return new AirParticle(pLevel, pX, pY, pZ, pXSpeed, pYSpeed, pZSpeed, this.sprites);
        }
    }
}
