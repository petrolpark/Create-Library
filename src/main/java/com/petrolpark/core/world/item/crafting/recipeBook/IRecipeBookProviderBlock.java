package com.petrolpark.core.world.item.crafting.recipeBook;

import java.util.HashSet;
import java.util.Queue;
import java.util.Set;
import java.util.stream.Stream;

import com.petrolpark.util.LinkedHashSetQueue;
import com.petrolpark.util.Pair;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public interface IRecipeBookProviderBlock {
    
    public boolean providesRecipeBook(RecipeHolder<?> recipeHolder, Level level, BlockPos pos, BlockState state);

    public Stream<RecipeHolder<?>> streamProvidedRecipeBooks(Level level, BlockPos pos, BlockState state);

    public default boolean shouldHighlightConnectedRecipeBookAcceptors(Level level, BlockPos pos, BlockState state) {
        return true;
    };

    public default Set<Pair<BlockPos, ProvisionType>> getRecipeBookProvisions(Level level, BlockPos pos, BlockState state) {
        Queue<BlockPos> acceptorPositions = new LinkedHashSetQueue<>();
        Set<BlockPos> checkedAcceptorPositions = new HashSet<>();
        Set<Pair<BlockPos, ProvisionType>> provisions = new HashSet<>();
        for (Direction face : Direction.values()) acceptorPositions.add(pos.relative(face));

        while (!acceptorPositions.isEmpty()) {
            BlockPos acceptorPos = acceptorPositions.poll();
            BlockState acceptorState = level.getBlockState(acceptorPos);
            if (!IBookRequiredRecipe.isRecipeBookAcceptor(level, acceptorPos, acceptorState)) continue;
            if (checkedAcceptorPositions.add(acceptorPos)) {
                provisions.add(Pair.of(acceptorPos, streamProvidedRecipeBooks(level, pos, state).anyMatch(rh -> IBookRequiredRecipe.acceptsRecipe(level, acceptorPos, acceptorState, rh)) ? ProvisionType.PROVIDES : ProvisionType.CAN_PROVIDE));
                IBookRequiredRecipe.addProxyRecipeBookAcceptorPositions(level, acceptorPos, acceptorState, acceptorPositions::add);
            };
        };

        return provisions;
    };

    public static void updateAvailableRecipes(Level level, BlockPos pos, BlockState state) {
        if (state.getBlock() instanceof IRecipeBookProviderBlock providerBlock) providerBlock.getRecipeBookProvisions(level, pos, state).stream()
            .map(Pair::getFirst)
            .forEach(acceptorPos -> {
                final BlockState acceptorState = level.getBlockState(acceptorPos);
                if (acceptorState.getBlock() instanceof IRecipeBookAcceptorBlock acceptorBlock) acceptorBlock.onAvailableRecipesChanged(level, acceptorPos, acceptorState);
                if (level.getBlockEntity(acceptorPos) instanceof IRecipeBookAcceptorBlockEntity acceptorBE) acceptorBE.onAvailableRecipesChanged();
            });
    };

    public static enum ProvisionType {
        CAN_PROVIDE,
        PROVIDES
    };
};
