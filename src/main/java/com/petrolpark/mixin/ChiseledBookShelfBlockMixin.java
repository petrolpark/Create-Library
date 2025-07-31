package com.petrolpark.mixin;

import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.spongepowered.asm.mixin.Mixin;

import com.petrolpark.config.PetrolparkConfigs;
import com.petrolpark.core.recipe.book.IRecipeBookProviderBlock;
import com.petrolpark.core.recipe.book.RecipeBookItem;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.ChiseledBookShelfBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

@Mixin(ChiseledBookShelfBlock.class)
public abstract class ChiseledBookShelfBlockMixin extends BaseEntityBlock implements IRecipeBookProviderBlock {

    protected ChiseledBookShelfBlockMixin(Properties properties) {
        super(properties);
        throw new AssertionError();
    };

    @Override
    public boolean providesRecipeBook(RecipeHolder<?> recipeHolder, Level level, BlockPos pos, BlockState state) {
        if (!PetrolparkConfigs.server().chiseledBookShelfProvidesRecipeBooks.get()) return false;
        return level.getBlockEntity(pos, BlockEntityType.CHISELED_BOOKSHELF).map(be -> be.hasAnyMatching(stack -> RecipeBookItem.streamProvidedRecipes(level, stack).anyMatch(recipeHolder::equals))).orElse(false);
    };

    @Override
    public Stream<RecipeHolder<?>> streamProvidedRecipeBooks(Level level, BlockPos pos, BlockState state) {
        if (!PetrolparkConfigs.server().chiseledBookShelfProvidesRecipeBooks.get()) return Stream.empty();
        return level.getBlockEntity(pos, BlockEntityType.CHISELED_BOOKSHELF).map(be -> IntStream.range(0, 6)
            .mapToObj(be::getItem)
            .flatMap(stack -> RecipeBookItem.streamProvidedRecipes(level, stack))
        ).orElse(Stream.empty());
    };

    @Override
    public boolean shouldHighlightConnectedRecipeBookAcceptors(Level level, BlockPos pos, BlockState state) {
        return PetrolparkConfigs.server().chiseledBookShelfProvidesRecipeBooks.get();
    };
    
};
