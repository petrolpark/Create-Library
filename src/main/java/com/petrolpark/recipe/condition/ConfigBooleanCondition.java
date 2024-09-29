package com.petrolpark.recipe.condition;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.petrolpark.Petrolpark;
import com.simibubi.create.foundation.config.ui.ConfigHelper;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.common.crafting.conditions.IConditionSerializer;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
public class ConfigBooleanCondition implements ICondition {

    public static final ResourceLocation ID = Petrolpark.asResource("config_boolean");
    public static final Serializer SERIALIZER = new Serializer();

    @SubscribeEvent
    public static void register(RegisterEvent event) {
        if (event.getRegistryKey().equals(ForgeRegistries.Keys.RECIPE_SERIALIZERS)) {
            // Conditions
            CraftingHelper.register(SERIALIZER);
        };
    };

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
            ConfigValue<?> configValue = ConfigHelper.findForgeConfigSpecFor(ModConfig.Type.COMMON, modId).getValues().get(ImmutableList.copyOf(Splitter.on(".").split(path)));
            if (!(configValue instanceof ForgeConfigSpec.BooleanValue booleanValue)) throw new JsonSyntaxException("The config must be a boolean type.");
            return new ConfigBooleanCondition(modId, booleanValue);
        };

        @Override
        public ResourceLocation getID() {
            return ID;
        };

    };
    
};
