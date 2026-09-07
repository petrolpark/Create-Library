package petrolpark.mc.library.core.data.reward.entity;

import javax.annotation.ParametersAreNonnullByDefault;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.NumberProviders;
import petrolpark.mc.library.core.data.numberProvider.NumberEstimate;
import petrolpark.mc.library.core.data.reward.info.IRewardInfo;
import petrolpark.mc.library.registry.PetrolparkRewardTypes;
import petrolpark.mc.library.util.Lang.IndentedTooltipBuilder;

@ParametersAreNonnullByDefault
public record MobEffectEntityReward(Holder<MobEffect> effect, NumberProvider duration, NumberProvider amplifier, boolean showParticles) implements IEntityReward {

    public static final MapCodec<MobEffectEntityReward> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        MobEffect.CODEC.fieldOf("effect").forGetter(MobEffectEntityReward::effect),
        NumberProviders.CODEC.fieldOf("duration").forGetter(MobEffectEntityReward::duration),
        NumberProviders.CODEC.optionalFieldOf("amplifier", ConstantValue.exactly(1f)).forGetter(MobEffectEntityReward::amplifier),
        Codec.BOOL.optionalFieldOf("show_particles", true).forGetter(MobEffectEntityReward::showParticles)
    ).apply(instance, MobEffectEntityReward::new));

    @Override
    public boolean reward(Entity entity, LootContext context, float multiplier, boolean simulate) {
        if (!(entity instanceof LivingEntity livingEntity)) return false;
        if (!simulate) 
            livingEntity.addEffect(new MobEffectInstance(effect(), Math.max(0, duration().getInt(context)), Math.max(0, amplifier().getInt(context)), false, showParticles()));
        return true;
    };

    @Override
    public MobEffectEntityReward.Info info() {
        return new MobEffectEntityReward.Info(effect(), NumberEstimate.get(duration()), NumberEstimate.get(amplifier()), showParticles());
    };

    public record Info(Holder<MobEffect> effect, NumberEstimate duration, NumberEstimate amplifier, boolean showParticles) implements IRewardInfo {

        public static final MapCodec<MobEffectEntityReward.Info> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            MobEffect.CODEC.fieldOf("effect").forGetter(MobEffectEntityReward.Info::effect),
            NumberEstimate.fieldCodec("duration").forGetter(MobEffectEntityReward.Info::duration),
            NumberEstimate.fieldCodec("amplifier").forGetter(MobEffectEntityReward.Info::amplifier),
            Codec.BOOL.optionalFieldOf("show_particles", true).forGetter(MobEffectEntityReward.Info::showParticles)
        ).apply(instance, MobEffectEntityReward.Info::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, MobEffectEntityReward.Info> STREAM_CODEC = StreamCodec.composite(
            MobEffect.STREAM_CODEC, MobEffectEntityReward.Info::effect,
            NumberEstimate.STREAM_CODEC, MobEffectEntityReward.Info::duration,
            NumberEstimate.STREAM_CODEC, MobEffectEntityReward.Info::amplifier,
            ByteBufCodecs.BOOL, MobEffectEntityReward.Info::showParticles,
            MobEffectEntityReward.Info::new
        );

        @Override
        public void render(GuiGraphics graphics) {
            graphics.blit(0, 0, 0, 16, 16, Minecraft.getInstance().getMobEffectTextures().get(effect()));
        };

        @Override
        public void addToDescription(IndentedTooltipBuilder builder) {
            MutableComponent mutableComponent = Component.translatable(effect().value().getDescriptionId());
            if (!amplifier().equals(NumberEstimate.ZERO))
                mutableComponent = Component.translatable("potion.withAmplifier", mutableComponent, amplifier().add(1f).getIntComponent());
            mutableComponent = Component.translatable("potion.withDuration", mutableComponent, duration().getTimeComponent());
        
        };

        @Override
        public EntityRewardAndInfoType getRewardInfoType() {
            return PetrolparkRewardTypes.ENTITY_MOB_EFFECT.get();
        };
    };

    @Override
    public EntityRewardAndInfoType getType() {
        return PetrolparkRewardTypes.ENTITY_MOB_EFFECT.get();
    };
    
};
