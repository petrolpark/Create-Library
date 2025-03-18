package com.petrolpark.recipe.condition;

import java.util.Optional;

import javax.annotation.Nonnull;

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.createmod.catnip.config.ui.ConfigHelper;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec.BooleanValue;
import net.neoforged.neoforge.common.ModConfigSpec.ConfigValue;
import net.neoforged.neoforge.common.conditions.ICondition;

public record ConfigBooleanCondition(String modid, String path) implements ICondition {

    //public static final ResourceLocation ID = Petrolpark.asResource("config_boolean");

    public static final MapCodec<ConfigBooleanCondition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        Codec.STRING.fieldOf("mod").forGetter(ConfigBooleanCondition::modid),
        Codec.STRING.fieldOf("value").forGetter(ConfigBooleanCondition::path)
    ).apply(instance, ConfigBooleanCondition::new)).validate(ConfigBooleanCondition::validate);

    @Override
    public boolean test(@Nonnull IContext context) {
        return getConfigValue().map(BooleanValue::get).orElse(false);
    };

    @Override
    public MapCodec<? extends ICondition> codec() {
        return CODEC;
    };

    public Optional<BooleanValue> getConfigValue() {
        try {
            ConfigValue<?> configValue = ConfigHelper.findModConfigSpecFor(ModConfig.Type.COMMON, modid).getValues().get(ImmutableList.copyOf(Splitter.on(".").split(path)));
            if (configValue instanceof BooleanValue booleanValue) return Optional.of(booleanValue);
        } catch (NullPointerException | ClassCastException e) {};
        return Optional.empty();
    };

    protected DataResult<ConfigBooleanCondition> validate() {
        if (getConfigValue().isPresent()) return DataResult.success(this);
        return DataResult.error(() -> "The config must be a boolean type");
    };
};
