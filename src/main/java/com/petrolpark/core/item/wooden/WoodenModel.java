package com.petrolpark.core.item.wooden;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import javax.annotation.Nonnull;

import org.jetbrains.annotations.Nullable;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.blaze3d.vertex.PoseStack;
import com.petrolpark.Petrolpark;
import com.petrolpark.PetrolparkDataComponentTypes;
import com.petrolpark.util.WoodHelper;
import com.petrolpark.util.WoodHelper.Wood;
import com.petrolpark.util.WoodHelperClient;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.model.BakedModelWrapper;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.client.model.data.ModelProperty;
import net.neoforged.neoforge.client.model.geometry.IGeometryBakingContext;
import net.neoforged.neoforge.client.model.geometry.IGeometryLoader;
import net.neoforged.neoforge.client.model.geometry.IUnbakedGeometry;

@EventBusSubscriber(Dist.CLIENT)
public class WoodenModel extends BakedModelWrapper<BakedModel> {

    public static final ModelProperty<Wood> WOOD_PROPERTY = new ModelProperty<>();

    protected final Map<Wood, BakedModel> models = new HashMap<>();

    public WoodenModel(BakedModel originalModel) {
        super(originalModel);
    };

    public BakedModel getModel(Wood wood) {
        return models.computeIfAbsent(wood, w -> WoodHelperClient.generateWoodModel(originalModel, w));
    };
    
    @Override
    public WoodenModel applyTransform(@Nonnull ItemDisplayContext cameraTransformType, @Nonnull PoseStack poseStack, boolean applyLeftHandTransform) {
        super.applyTransform(cameraTransformType, poseStack, applyLeftHandTransform);
        return this;
    };

    @Override
    public List<BakedModel> getRenderPasses(@Nonnull ItemStack stack, boolean fabulous) {
        return Collections.singletonList(getModel(stack.getOrDefault(PetrolparkDataComponentTypes.WOOD, WoodHelper.OAK)));
    };

    @Override
    @SuppressWarnings("null")
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, RandomSource rand, ModelData extraData, @Nullable RenderType renderType) {
        return getModel(extraData.get(WOOD_PROPERTY)).getQuads(state, side, rand, extraData, renderType);
    };

    public record Unbaked(BlockModel baseModel) implements IUnbakedGeometry<WoodenModel.Unbaked> {

        @Override
        public WoodenModel bake(@Nonnull IGeometryBakingContext context, @Nonnull ModelBaker baker, @Nonnull Function<Material, TextureAtlasSprite> spriteGetter, @Nonnull ModelState modelState, @Nonnull ItemOverrides overrides) {
            baseModel().resolveParents(baker::getModel);
            return new WoodenModel(baseModel().bake(baker, spriteGetter, modelState));
        };

    };

    public static class Loader implements IGeometryLoader<WoodenModel.Unbaked> {

        public static final ResourceLocation ID = Petrolpark.asResource("wooden");
        public static final WoodenModel.Loader INSTANCE = new WoodenModel.Loader();

        @Override
        public WoodenModel.Unbaked read(@Nonnull JsonObject jsonObject, @Nonnull JsonDeserializationContext deserializationContext) throws JsonParseException {
            if (!jsonObject.has("template")) throw new JsonParseException("Must specify a template");
            return new WoodenModel.Unbaked(deserializationContext.deserialize(jsonObject.get("template"), BlockModel.class));
        };

    };

    @SubscribeEvent
    public static final void onRegisterGeometryLoaders(ModelEvent.RegisterGeometryLoaders event) {
        event.register(WoodenModel.Loader.ID, WoodenModel.Loader.INSTANCE);
    };
    
};
