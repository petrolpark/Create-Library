package com.petrolpark.recipe.condition;

import org.apache.commons.compress.utils.Lists;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.petrolpark.Petrolpark;
import com.simibubi.create.foundation.config.ui.ConfigHelper;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.common.crafting.conditions.IConditionSerializer;
import net.minecraftforge.fml.config.ModConfig;

public class ConfigBooleanCondition implements ICondition {

    public static final ResourceLocation ID = Petrolpark.asResource("config_boolean");
    public static final Serializer SERIALIZER = new Serializer();

    private final String modId;
    private final ForgeConfigSpec.BooleanValue value;

    public ConfigBooleanCondition(String modId, ForgeConfigSpec.BooleanValue value) {
        this.modId = modId;
        this.value = value;
    };

    @Override
    public ResourceLocation getID() {
        return ID;
    };

    @Override
    public boolean test(IContext context) {
        return value != null && value.get();
    };

    public static class Serializer implements IConditionSerializer<ConfigBooleanCondition> {

        @Override
        public void write(JsonObject json, ConfigBooleanCondition value) {
            json.addProperty("value", Joiner.on(".").join(value.value.getPath().iterator()));
            json.addProperty("mod", value.modId);
        };

        @Override
        public ConfigBooleanCondition read(JsonObject json) {
            if (!json.has("value")) throw new JsonSyntaxException("Must specify a config boolean");
            if (!json.has("mod")) throw new JsonSyntaxException("Must specify a mod ID");
            String path = GsonHelper.getAsString(json, "value");
            String modId = GsonHelper.getAsString(json, "mod");
            ConfigValue<?> configValue = ConfigHelper.findForgeConfigSpecFor(ModConfig.Type.COMMON, modId).getValues().get(Lists.newArrayList(Splitter.on(".").split(path).iterator()));
            if (!(configValue instanceof ForgeConfigSpec.BooleanValue booleanValue)) throw new JsonSyntaxException("The config must be a boolean type.");
            return new ConfigBooleanCondition(modId, booleanValue);
        };

        @Override
        public ResourceLocation getID() {
            return ID;
        };

    };
    
};
