package com.petrolpark.util;

import static com.petrolpark.util.WoodHelper.OAK;
import static com.petrolpark.util.WoodHelper.getDoorBlockOrOak;
import static com.petrolpark.util.WoodHelper.getLogBlockOrOak;
import static com.petrolpark.util.WoodHelper.getPlanksBlockOrOak;
import static com.petrolpark.util.WoodHelper.getStrippedLogBlockOrOak;
import static com.petrolpark.util.WoodHelper.getTrapdoorBlockOrOak;

import java.util.Map;
import java.util.function.UnaryOperator;

import javax.annotation.Nullable;

import com.petrolpark.core.client.rendering.PetrolparkBakedModelHelper;
import com.petrolpark.util.WoodHelper.Wood;

import it.unimi.dsi.fastutil.objects.Reference2ReferenceOpenHashMap;
import net.createmod.catnip.render.StitchedSprite;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class WoodHelperClient {
    
    @OnlyIn(Dist.CLIENT)
    public static final StitchedSprite 
    PLANKS_TEMPLATE = new StitchedSprite(ResourceLocation.withDefaultNamespace("block/oak_planks")),
	LOG_SIDE_TEMPLATE = new StitchedSprite(ResourceLocation.withDefaultNamespace("block/oak_log")),
    LOG_TOP_TEMPLATE = new StitchedSprite(ResourceLocation.withDefaultNamespace("block/oak_log_top")),
    STRIPPED_LOG_SIDE_TEMPLATE = new StitchedSprite(ResourceLocation.withDefaultNamespace("block/stripped_oak_log")),
    STRIPPED_LOG_TOP_TEMPLATE = new StitchedSprite(ResourceLocation.withDefaultNamespace("block/stripped_oak_log_top")),
    LEAVES_TEMPLATE = new StitchedSprite(ResourceLocation.withDefaultNamespace("block/oak_leaves")),
    DOOR_TOP_TEMPLATE = new StitchedSprite(ResourceLocation.withDefaultNamespace("block/oak_door_top")),
    DOOR_BOTTOM_TEMPLATE = new StitchedSprite(ResourceLocation.withDefaultNamespace("block/oak_door_bottom")),
    TRAPDOOR_TEMPLATE = new StitchedSprite(ResourceLocation.withDefaultNamespace("block/oak_trapdoor"));

    @Nullable
    public static final BakedModel generateWoodModel(BakedModel template, Wood wood) {
        if (PLANKS_TEMPLATE.get() == null) return null; // Not loaded yet
		if (wood == null || OAK.equals(wood)) return PetrolparkBakedModelHelper.swapSprites(template, UnaryOperator.identity());

		final BlockState logState = getLogBlockOrOak(wood).defaultBlockState();
        final BlockState strippedLogState = getStrippedLogBlockOrOak(wood).defaultBlockState();
        final BlockState doorBottomState = getDoorBlockOrOak(wood).defaultBlockState();

		final Map<TextureAtlasSprite, TextureAtlasSprite> map = new Reference2ReferenceOpenHashMap<>();
		map.put(PLANKS_TEMPLATE.get(), PetrolparkBakedModelHelper.getSpriteOnSide(getPlanksBlockOrOak(wood).defaultBlockState(), Direction.UP));
		map.put(LOG_SIDE_TEMPLATE.get(), PetrolparkBakedModelHelper.getSpriteOnSide(logState, Direction.SOUTH));
		map.put(LOG_TOP_TEMPLATE.get(), PetrolparkBakedModelHelper.getSpriteOnSide(logState, Direction.UP));
        map.put(STRIPPED_LOG_SIDE_TEMPLATE.get(), PetrolparkBakedModelHelper.getSpriteOnSide(strippedLogState, Direction.SOUTH));
		map.put(STRIPPED_LOG_TOP_TEMPLATE.get(), PetrolparkBakedModelHelper.getSpriteOnSide(strippedLogState, Direction.UP));
        map.put(LEAVES_TEMPLATE.get(), PetrolparkBakedModelHelper.getSpriteOnSide(Blocks.OAK_LEAVES.defaultBlockState(), Direction.UP));
        map.put(DOOR_BOTTOM_TEMPLATE.get(), PetrolparkBakedModelHelper.getSpriteOnSide(doorBottomState, Direction.SOUTH));
        map.put(DOOR_TOP_TEMPLATE.get(), PetrolparkBakedModelHelper.getSpriteOnSide(doorBottomState.setValue(DoorBlock.HALF, DoubleBlockHalf.UPPER), Direction.SOUTH));
        map.put(TRAPDOOR_TEMPLATE.get(), PetrolparkBakedModelHelper.getSpriteOnSide(getTrapdoorBlockOrOak(wood).defaultBlockState(), Direction.UP));

		return PetrolparkBakedModelHelper.swapSprites(template, map::get);
	};

    public static final void init() {};
};
