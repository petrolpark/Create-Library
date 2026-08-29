package petrolpark.mc.library.shared.client.particle;

import javax.annotation.Nonnull;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.particles.SimpleParticleType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import petrolpark.mc.library.shared.registry.SharedParticleTypes;

@OnlyIn(Dist.CLIENT)
@EventBusSubscriber(Dist.CLIENT)
public class TearParticle extends TextureSheetParticle {

    public TearParticle(ClientLevel level, double x, double y, double z, double vx, double vy, double vz, SpriteSet spriteSet) {
        super(level, x, y, z);
        
        setSize(0.01f, 0.01f);
        pickSprite(spriteSet);
        setColor(203 / 255f, 242 / 255f, 240 / 255f);
        xd = vx;
        yd = vy;
        zd = vz;
        gravity = 0.16f;
    };

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
    };

    @OnlyIn(Dist.CLIENT)
    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet spriteSet;

        public Provider(SpriteSet sprites) {
            this.spriteSet = sprites;
        };

        public Particle createParticle(@Nonnull SimpleParticleType type, @Nonnull ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            TearParticle particle = new TearParticle(level, x, y, z, xSpeed, ySpeed, zSpeed, spriteSet);
            return particle;
        };
    };

    @SubscribeEvent
    public static final void onRegisterParticleProviders(RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(SharedParticleTypes.TEAR.get(), TearParticle.Provider::new);
    };
    
};
