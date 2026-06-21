package petrolpark.mc.library.shared.world.item.crafting;

import java.util.Collections;
import java.util.Optional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import petrolpark.mc.library.PetrolparkTags;
import petrolpark.mc.library.core.data.recipe.IBiomeSpecificRecipe;
import petrolpark.mc.library.shared.registry.SharedRecipeSerializers;
import petrolpark.mc.library.shared.registry.SharedRecipeTypes;
import petrolpark.mc.library.util.codec.CodecHelper;

import net.createmod.catnip.codecs.stream.CatnipStreamCodecs;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.critereon.BlockPredicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FarmBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.minecraft.world.level.gameevent.GameEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.event.entity.player.UseItemOnBlockEvent;

@EventBusSubscriber
public record CropFertilizingRecipe(BlockPredicate crop, Optional<BlockPredicate> soil, Optional<BlockPredicate> subsoil, Ingredient fertilizer, Optional<HolderSet<Biome>> biomes, BlockState result, Optional<BlockState> soilResult, Optional<BlockState> subsoilResult) implements Recipe<CropFertilizingRecipe.Input>, IBiomeSpecificRecipe {
    
    public static final MapCodec<CropFertilizingRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        BlockPredicate.CODEC.fieldOf("crop").forGetter(CropFertilizingRecipe::crop),
        BlockPredicate.CODEC.optionalFieldOf("soil").forGetter(CropFertilizingRecipe::soil),
        BlockPredicate.CODEC.optionalFieldOf("subsoil").forGetter(CropFertilizingRecipe::subsoil),
        Ingredient.CODEC.fieldOf("fertilizer").forGetter(CropFertilizingRecipe::fertilizer),
        RegistryCodecs.homogeneousList(Registries.BIOME).optionalFieldOf("biomes").forGetter(CropFertilizingRecipe::biomes),
        BlockState.CODEC.fieldOf("result").forGetter(CropFertilizingRecipe::result),
        BlockState.CODEC.optionalFieldOf("soil_result").forGetter(CropFertilizingRecipe::soilResult),
        BlockState.CODEC.optionalFieldOf("subsoil_result").forGetter(CropFertilizingRecipe::subsoilResult)
    ).apply(instance, CropFertilizingRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, CropFertilizingRecipe> STREAM_CODEC = CodecHelper.compositeStreamCodec(
        BlockPredicate.STREAM_CODEC, CropFertilizingRecipe::crop,
        ByteBufCodecs.optional(BlockPredicate.STREAM_CODEC), CropFertilizingRecipe::soil,
        ByteBufCodecs.optional(BlockPredicate.STREAM_CODEC), CropFertilizingRecipe::subsoil,
        Ingredient.CONTENTS_STREAM_CODEC, CropFertilizingRecipe::fertilizer,
        ByteBufCodecs.optional(ByteBufCodecs.holderSet(Registries.BIOME)), CropFertilizingRecipe::biomes,
        CatnipStreamCodecs.BLOCK_STATE, CropFertilizingRecipe::result,
        ByteBufCodecs.optional(CatnipStreamCodecs.BLOCK_STATE), CropFertilizingRecipe::soilResult,
        ByteBufCodecs.optional(CatnipStreamCodecs.BLOCK_STATE), CropFertilizingRecipe::subsoilResult,
        CropFertilizingRecipe::new
    );
    
    public static final boolean apply(Level level, BlockPos cropPos, ItemStack stack, @Nullable Player player) {
        final CropFertilizingRecipe.Input input = new CropFertilizingRecipe.Input(stack, new BlockInWorld(level, cropPos, false), new BlockInWorld(level, cropPos.below(), false), new BlockInWorld(level, cropPos.below(2), false), level.getBiome(cropPos));
        return level.getRecipeManager().getRecipeFor(SharedRecipeTypes.CROP_FERTILIZING.get(), input, level).map(recipeHolder -> {
            final CropFertilizingRecipe recipe = recipeHolder.value();

            level.setBlockAndUpdate(cropPos, recipe.result());
            if (recipe.shouldSetSoil()) level.setBlockAndUpdate(cropPos.below(), recipe.getResultSoilState(level.getBlockState(cropPos.below())));
            if (recipe.shouldSetSubSoil()) level.setBlockAndUpdate(cropPos.below(2), recipe.getResultSubsoilState(level.getBlockState(cropPos.below(2))));
            
            if (player instanceof ServerPlayer serverPlayer) {
                serverPlayer.gameEvent(GameEvent.ITEM_INTERACT_FINISH);
                CriteriaTriggers.RECIPE_CRAFTED.trigger(serverPlayer, recipeHolder.id(), Collections.singletonList(stack));
            };
            
            stack.shrink(1);
            level.levelEvent(1505, cropPos, 15); // Send particles

            return true;
        }).orElse(false);
    };

    public static final ItemStack dispense(BlockSource blockSource, ItemStack stack) {
        apply(blockSource.level(), blockSource.pos(), stack, null);
        return stack;
    };

    public ItemStack getResultStack() {
        return new ItemStack(result().getBlock());
    };

    public boolean shouldSetSoil() {
        return (soilResult().isPresent() || soil().isPresent());
    };

    public BlockState getResultSoilState(BlockState soilState) {
        if (soilResult().isPresent()) return soilResult().get();
        if (soil().isPresent()) {
            if (soilState.getBlock() instanceof FarmBlock) return Blocks.FARMLAND.defaultBlockState();
            return Blocks.DIRT.defaultBlockState();
        };
        return soilState;
    };

    public boolean shouldSetSubSoil() {
        return subsoilResult().isPresent() || subsoil().isPresent();  
    };

    public BlockState getResultSubsoilState(BlockState subsoilState) {
        if (subsoilResult().isPresent()) return subsoilResult().get();
        if (subsoil().isPresent()) {
            if (subsoilState.is(Tags.Blocks.ORES_IN_GROUND_DEEPSLATE)) return Blocks.DEEPSLATE.defaultBlockState();
            if (subsoilState.is(Tags.Blocks.ORES_IN_GROUND_NETHERRACK)) return Blocks.NETHERRACK.defaultBlockState();
            if (subsoilState.is(PetrolparkTags.commonBlockTag("ores_in_ground/end_stone"))) return Blocks.END_STONE.defaultBlockState();
            return Blocks.STONE.defaultBlockState();
        };
        return subsoilState;
    };

    @Override
    public Optional<HolderSet<Biome>> getAllowedBiomes() {
        return biomes();
    };

    @Override
    public boolean matches(@Nonnull CropFertilizingRecipe.Input input, @Nonnull Level level) {
        return fertilizer().test(input.fertilizer())
            && crop().matches(input.crop())
            && soil().map(soil -> soil.matches(input.soil())).orElse(true)
            && subsoil().map(subsoil -> subsoil.matches(input.subsoil())).orElse(true)
            && IBiomeSpecificRecipe.isValidIn(biomes(), input.biome());
    };

    @Override
    public ItemStack assemble(@Nonnull CropFertilizingRecipe.Input input, @Nonnull HolderLookup.Provider registries) {
        return getResultStack();
    };

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return false;
    };

    @Override
    public ItemStack getResultItem(@Nonnull HolderLookup.Provider registries) {
        return getResultStack();
    };

    @Override
    public RecipeSerializer<?> getSerializer() {
        return SharedRecipeSerializers.CROP_FERTILIZING.get();
    };

    @Override
    public RecipeType<?> getType() {
        return SharedRecipeTypes.CROP_FERTILIZING.get();
    };

    public static final record Input(ItemStack fertilizer, BlockInWorld crop, BlockInWorld soil, BlockInWorld subsoil, Holder<Biome> biome) implements RecipeInput {

        @Override
        public ItemStack getItem(int index) {
            if (index != 0) throw new IllegalArgumentException("Index out of range");
            return fertilizer();
        };

        @Override
        public int size() {
            return 1;
        };
    };

    @SubscribeEvent
    public static final void onUseItemOnBlock(UseItemOnBlockEvent event) {
        if (apply(event.getLevel(), event.getPos(), event.getItemStack(), event.getPlayer())) event.cancelWithResult(ItemInteractionResult.CONSUME);
    };
};
