package com.petrolpark.core.data.condition;

import java.util.Arrays;

import javax.annotation.Nonnull;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.petrolpark.util.Pair;
import com.petrolpark.util.codec.CodecHelper;

import net.createmod.catnip.config.ui.ConfigHelper;
import net.createmod.catnip.config.ui.ConfigHelper.ConfigPath;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.ModConfigSpec.ConfigValue;
import net.neoforged.neoforge.common.conditions.ICondition;

public record ConfigBooleanCondition(Pair<String, ConfigValue<Boolean>> value) implements ICondition {

    public static final MapCodec<ConfigBooleanCondition> CODEC = CodecHelper.singleFieldMap(Codec.STRING.flatXmap(
        key -> {
            final ConfigPath path;
            try {
                path = ConfigPath.parse(key);
            } catch (IllegalArgumentException e) {
                return DataResult.error(e::getMessage);
            };
            if (path.getType() != ModConfig.Type.COMMON) return DataResult.error(() -> "Config value: '" + path.toString() + "' is not a common config (cannot use per-world or client configs)");
            final ModConfigSpec spec = ConfigHelper.findModConfigSpecFor(path.getType(), path.getModID());
            if (spec == null) return DataResult.error(() -> "Mod " + path.getModID() + " does not have a common config");
            final ConfigValue<Boolean> configValue;
            try {
                configValue = spec.getValues().get(Arrays.asList(path.getPath()));
            } catch (ClassCastException e) {
                return DataResult.error(() -> "Config value '" + path.toString() + "' is not a boolean");
            };
            if (configValue == null) return DataResult.error(() -> "No such key: '" + path.toString() + "'");
            return DataResult.success(Pair.of(path.getModID(), configValue));
        },
        value -> DataResult.success(new ConfigPath()
            .setType(ModConfig.Type.COMMON)
            .setID(value.getFirst())
            .setPath(value.getSecond().getPath().toArray(String[]::new))
            .toString()
        )
    ), "key", ConfigBooleanCondition::value, ConfigBooleanCondition::new);

    @Override
    public boolean test(@Nonnull IContext context) {
        return value.getSecond().get();
    };

    @Override
    public MapCodec<ConfigBooleanCondition> codec() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'codec'");
    };
    
};
