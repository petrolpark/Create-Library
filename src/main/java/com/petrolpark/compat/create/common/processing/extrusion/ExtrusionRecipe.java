package com.petrolpark.compat.create.common.processing.extrusion;

import javax.annotation.Nonnull;

import org.jetbrains.annotations.Nullable;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.petrolpark.Petrolpark;
import com.petrolpark.compat.create.CreateRecipeTypes;
import com.simibubi.create.api.behaviour.movement.MovementBehaviour;
import com.simibubi.create.api.registry.SimpleRegistry;

import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.RecipesUpdatedEvent;

public record ExtrusionRecipe(HolderSet<Block> inputs, BlockState output) implements Recipe<ExtrusionRecipe.Input> {

    static {
        MovementBehaviour.REGISTRY.registerProvider(Petrolpark.EXTRUSION_MOVEMENT_BEHAVIOUR_PROVIDER);
    };

    public static final MapCodec<ExtrusionRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        RegistryCodecs.homogeneousList(Registries.BLOCK).fieldOf("inputs").forGetter(ExtrusionRecipe::inputs),
        BlockState.CODEC.fieldOf("output").forGetter(ExtrusionRecipe::output)
    ).apply(instance, ExtrusionRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, ExtrusionRecipe> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.holderSet(Registries.BLOCK), ExtrusionRecipe::inputs,
        ByteBufCodecs.idMapper(Block.BLOCK_STATE_REGISTRY), ExtrusionRecipe::output,
        ExtrusionRecipe::new
    );

    @Override
    public boolean matches(@Nonnull Input input, @Nonnull Level level) {
        return input.state().is(inputs());
    };

    @Override
    public ItemStack assemble(@Nonnull Input input, @Nonnull HolderLookup.Provider registries) {
        return new ItemStack(extrude(input.state(), input.extrusionDirection()).getBlock());
    };

    public BlockState extrude(@Nonnull BlockState input, @Nullable Direction extrusionDirection) {
        if (input.is(inputs())) {
            BlockState output = output();
            if (extrusionDirection != null) {
                if (output.hasProperty(BlockStateProperties.FACING)) output = output().setValue(BlockStateProperties.FACING, extrusionDirection);
                else if (output.hasProperty(BlockStateProperties.AXIS)) output = output().setValue(BlockStateProperties.AXIS, extrusionDirection.getAxis());
            };
            return output;
        } else {
            return input;
        }
    };

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return false;
    };

    @Override
    public ItemStack getResultItem(@Nonnull HolderLookup.Provider registries) {
        return new ItemStack(output().getBlock());
    };

    @Override
    public RecipeSerializer<ExtrusionRecipe> getSerializer() {
       return CreateRecipeTypes.EXTRUSION.getSerializer();
    };

    @Override
    public RecipeType<ExtrusionRecipe> getType() {
        return CreateRecipeTypes.EXTRUSION.getType();
    };

    
    public static record Input(BlockState state, Direction extrusionDirection) implements RecipeInput {

        @Override
        public ItemStack getItem(int index) {
            return new ItemStack(state.getBlock());
        };

        @Override
        public int size() {
            return 1;
        };

    };

    public static class Serializer implements RecipeSerializer<ExtrusionRecipe> {

        @Override
        public MapCodec<ExtrusionRecipe> codec() {
            return CODEC;
        };

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, ExtrusionRecipe> streamCodec() {
            return STREAM_CODEC;
        };

    };

    public static class MovementBehaviourProvider implements SimpleRegistry.Provider<Block, MovementBehaviour> {

        private RecipeManager recipeManager = null;

        @Override
        @SuppressWarnings("deprecation")
        public @Nullable MovementBehaviour get(Block block) {
            if (recipeManager == null) return null;
            return recipeManager.getAllRecipesFor(CreateRecipeTypes.EXTRUSION.getType(ExtrusionRecipe.class)).stream()
                .filter(rh -> 
                    rh.value().inputs().contains(block.builtInRegistryHolder())
                )
                .findFirst()
                .map(ExtrusionMovementBehaviour::new)
                .orElse(null);
        };

        @SubscribeEvent
        public final void onRecipesUpdated(RecipesUpdatedEvent event) {
            recipeManager = event.getRecipeManager();
            MovementBehaviour.REGISTRY.invalidate();
        };

    };
};
