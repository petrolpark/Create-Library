package petrolpark.mc.library.compat.create.core.world.dough.client;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.collect.Maps;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.foundation.model.BakedModelHelper;

import net.createmod.catnip.data.Iterate;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.client.resources.model.SimpleBakedModel;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.RenderTypeGroup;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.model.BakedModelWrapper;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.client.model.data.ModelProperty;
import net.neoforged.neoforge.client.model.geometry.IGeometryBakingContext;
import net.neoforged.neoforge.client.model.geometry.IGeometryLoader;
import net.neoforged.neoforge.client.model.geometry.IUnbakedGeometry;
import petrolpark.mc.library.Petrolpark;
import petrolpark.mc.library.core.client.rendering.PetrolparkBakedModelHelper;

public class RolledDoughModel extends BakedModelWrapper<BakedModel> {

    public static final ModelProperty<DoughRenderer> DOUGH_PROPERTY = new ModelProperty<>();

    public RolledDoughModel(BakedModel originalModel) {
        super(originalModel);
    };
    
    public BakedModel getOriginalModel() {
        return originalModel;
    };

    @SuppressWarnings("null")
    public SimpleBakedModel getShaped(AABB aabb, TextureAtlasSprite sprite, RandomSource rand) {
        final Map<Direction, List<BakedQuad>> culledQuads = Maps.newEnumMap(Direction.class);
        final List<BakedQuad> quads = new ArrayList<>();
        for (final Direction side : Iterate.directions) {
            culledQuads.put(side, Collections.emptyList());
            for (final BakedQuad quad : super.getQuads(null, side, rand)) {
                quads.add(PetrolparkBakedModelHelper.copyWithGeometryAndSprite(quad,
                    BakedModelHelper.cropAndMove(quad.getVertices(), quad.getSprite(), aabb, Vec3.ZERO),
                    sprite
                ));
            };
        };
        return new SimpleBakedModel(quads, culledQuads, useAmbientOcclusion(), usesBlockLight(), isGui3d(), getParticleIcon(), getTransforms(), getOverrides(), RenderTypeGroup.EMPTY);
    };

    /**
     * Returns empty List.
     * Rendering is handled by {@link DoughRenderer}.
     */
    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @Nonnull RandomSource rand, @Nonnull ModelData extraData,  @Nullable RenderType renderType) {
        return Collections.emptyList();
    };

    @Override
    public boolean isCustomRenderer() {
        return true;
    };

    @Override
    public RolledDoughModel applyTransform(@Nonnull ItemDisplayContext cameraTransformType, @Nonnull PoseStack poseStack, boolean applyLeftHandTransform) {
        super.applyTransform(cameraTransformType, poseStack, applyLeftHandTransform);
        return this;
    };

    @Override
    public TextureAtlasSprite getParticleIcon(@Nonnull ModelData data) {
        final DoughRenderer renderingData = data.get(DOUGH_PROPERTY);
        return renderingData == null || renderingData.data == null ? super.getParticleIcon(data) : Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(renderingData.data.dough().textureLocation());
    };

    @Override
    public TextureAtlasSprite getParticleIcon() {
        return Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(MissingTextureAtlasSprite.getLocation());
    };

    public record Unbaked(BlockModel baseModel) implements IUnbakedGeometry<RolledDoughModel.Unbaked> {

        @Override
        public RolledDoughModel bake(@Nonnull IGeometryBakingContext context, @Nonnull ModelBaker baker, @Nonnull Function<Material, TextureAtlasSprite> spriteGetter, @Nonnull ModelState modelState, @Nonnull ItemOverrides overrides) {
            baseModel().resolveParents(baker::getModel);
            return new RolledDoughModel(baseModel().bake(baker, spriteGetter, modelState));
        };

    };

    public static class Loader implements IGeometryLoader<RolledDoughModel.Unbaked> {

        public static final ResourceLocation ID = Petrolpark.asResource("dough");
        public static final RolledDoughModel.Loader INSTANCE = new RolledDoughModel.Loader();

        @Override
        public RolledDoughModel.Unbaked read(@Nonnull JsonObject jsonObject, @Nonnull JsonDeserializationContext deserializationContext) throws JsonParseException {
            if (!jsonObject.has("base")) throw new JsonParseException("Must specify a base");
            return new RolledDoughModel.Unbaked(deserializationContext.deserialize(jsonObject.get("base"), BlockModel.class));
        };

    };

    public static final void onRegisterGeometryLoaders(ModelEvent.RegisterGeometryLoaders event) {
        event.register(RolledDoughModel.Loader.ID, RolledDoughModel.Loader.INSTANCE);
    };
    
};
