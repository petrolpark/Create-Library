package com.petrolpark.core.recipe.book;

import java.util.HashSet;
import java.util.Queue;
import java.util.Set;
import java.util.function.Consumer;

import javax.annotation.Nullable;

import com.petrolpark.core.recipe.INamedRecipe;
import com.petrolpark.util.LinkedHashSetQueue;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public interface IBookRequiredRecipe extends INamedRecipe {

    public boolean isBookRequired(Level level);

    @Nullable
    public static IBookRequiredRecipe checkedCast(Recipe<?> recipe) {
        return recipe instanceof IBookRequiredRecipe bnr ? bnr : null;
    };

    public static boolean hasRequiredBook(Level level, BlockPos pos, RecipeHolder<?> recipeHolder) {
        Queue<BlockPos> acceptorPositions = new LinkedHashSetQueue<>();
        Set<BlockPos> checkedAcceptorPositions = new HashSet<>();
        acceptorPositions.add(pos);
        
        while (!acceptorPositions.isEmpty()) {
            BlockPos acceptorPos = acceptorPositions.poll();
            BlockState acceptorState = level.getBlockState(acceptorPos);
            if (!isRecipeBookAcceptor(level, acceptorPos, acceptorState)) continue;
            if (checkedAcceptorPositions.add(acceptorPos)) {
                for (Direction face : Direction.values()) {
                    BlockPos providerPos = acceptorPos.relative(face);
                    BlockState providerState = level.getBlockState(providerPos);
                    if (
                        providerState.getBlock() instanceof IRecipeBookProviderBlock rbpBlock
                        && rbpBlock.providesRecipeBook(recipeHolder, level, providerPos, providerState)
                    ) return true;
                };
                addProxyRecipeBookAcceptorPositions(level, acceptorPos, acceptorState, acceptorPositions::add);
            };
        };

        return false;
    };

    public static boolean isRecipeBookAcceptor(Level level, BlockPos pos, BlockState state) {
        return state.getBlock() instanceof IRecipeBookAcceptorBlock || level.getBlockEntity(pos) instanceof IRecipeBookAcceptorBlockEntity;
    };

    public static boolean acceptsRecipe(Level level, BlockPos pos, BlockState state, RecipeHolder<?> recipeHolder) {
        if (state.getBlock() instanceof IRecipeBookAcceptorBlock rbaBlock && rbaBlock.acceptsRecipeBook(level, pos, state, recipeHolder)) return true;
        if (level.getBlockEntity(pos) instanceof IRecipeBookAcceptorBlockEntity rbaBlockEntity && rbaBlockEntity.acceptsRecipeBook(recipeHolder)) return true;
        return false;
    };

    public static void addProxyRecipeBookAcceptorPositions(Level level, BlockPos acceptorPos, BlockState acceptorState, Consumer<BlockPos> posAdder) {
        if (acceptorState.getBlock() instanceof IRecipeBookAcceptorBlock rbaBlock) rbaBlock.addProxyRecipeBookAcceptorPositions(level, acceptorPos, acceptorState, posAdder);
        if (level.getBlockEntity(acceptorPos) instanceof IRecipeBookAcceptorBlockEntity rbaBlockEntity) rbaBlockEntity.addProxyRecipeBookAcceptorPositions(posAdder);
    };
};
