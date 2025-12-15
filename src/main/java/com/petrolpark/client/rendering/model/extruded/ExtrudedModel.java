package com.petrolpark.client.rendering.model.extruded;

import java.util.function.Function;

import javax.annotation.Nonnull;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.serialization.JsonOps;
import com.petrolpark.Petrolpark;
import com.petrolpark.util.Mask;

import net.minecraft.client.renderer.block.model.BlockFaceUV;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.model.geometry.IGeometryBakingContext;
import net.neoforged.neoforge.client.model.geometry.IGeometryLoader;
import net.neoforged.neoforge.client.model.geometry.IUnbakedGeometry;

@OnlyIn(Dist.CLIENT)
@EventBusSubscriber(Dist.CLIENT)
public record ExtrudedModel(BlockModel baseModel, Mask mask, float minZ, float maxZ) implements IUnbakedGeometry<ExtrudedModel> {

    public static final ResourceLocation LOADER_ID = Petrolpark.asResource("extruded");
    public static final ExtrudedModel.Loader LOADER = new ExtrudedModel.Loader();

    public static final ExtrudedModelGenerator EXTRUDED_MODEL_GENERATOR = new ExtrudedModelGenerator();

    @Override
    public BakedModel bake(@Nonnull IGeometryBakingContext context, @Nonnull ModelBaker baker, @Nonnull Function<Material, TextureAtlasSprite> spriteGetter, @Nonnull ModelState modelState, @Nonnull ItemOverrides overrides) {
        return EXTRUDED_MODEL_GENERATOR.generateExtrudedModel(mask(), minZ(), maxZ(), new BlockFaceUV(new float[]{0f, 0f, 16f, 16f}, 0), spriteGetter, baseModel()).bakeVanilla(baker, baseModel(), spriteGetter, modelState, context.isGui3d());
    };

    public static final class Loader implements IGeometryLoader<ExtrudedModel> {

        @Override
        public ExtrudedModel read(@Nonnull JsonObject jsonObject, @Nonnull JsonDeserializationContext deserializationContext) throws JsonParseException {
            if (!jsonObject.has("base")) throw new JsonParseException("Must specify a base model to provide the textures");
            final BlockModel baseModel = deserializationContext.deserialize(jsonObject.get("base"), BlockModel.class);
            final Mask mask = Mask.FRIENDLY_CODEC.parse(JsonOps.INSTANCE, jsonObject.get("mask")).getOrThrow(JsonParseException::new);
            
            return new ExtrudedModel(baseModel, mask, 0, 4f); // testing only
        };

    };

    @SubscribeEvent
    public static final void onRegisterGeometryLoaders(ModelEvent.RegisterGeometryLoaders event) {
        event.register(LOADER_ID, LOADER);
    };
    
};
