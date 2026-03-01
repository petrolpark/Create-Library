package com.petrolpark.mixin;

import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.petrolpark.config.PetrolparkConfigs;
import com.petrolpark.core.recipe.book.IRecipeBookProviderBlock;
import com.petrolpark.core.recipe.book.RecipeBookItem;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.ChiseledBookShelfBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.ChiseledBookShelfBlockEntity;
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
        return PetrolparkConfigs.server().chiseledBookShelfProvidesRecipeBooks.get() && streamProvidedRecipeBooks(level, pos, state).count() > 0;
    };

    @Inject(
        method = "addBook",
        at = @At("TAIL")
    )
    private static final void petrolpark$updateAvailableRecipeBooks(Level level, BlockPos pos, Player player, ChiseledBookShelfBlockEntity blockEntity, ItemStack bookStack, int slot, CallbackInfo ci) {
        IRecipeBookProviderBlock.updateAvailableRecipes(level, pos, level.getBlockState(pos));
    };

    @Inject(
        method = "removeBook",
        at = @At("TAIL")
    )
    private static final void petrolpark$updateAvailableRecipeBooks(Level level, BlockPos pos, Player player, ChiseledBookShelfBlockEntity blockEntity, int slot, CallbackInfo ci) {
        IRecipeBookProviderBlock.updateAvailableRecipes(level, pos, level.getBlockState(pos));
    };

    @Inject(
        method = "onRemove",
        at = @At("HEAD")
    )
    protected void petroplark$updateAvailableRecipeBooks(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston, CallbackInfo ci) {
        IRecipeBookProviderBlock.updateAvailableRecipes(level, pos, state);
    };
    
};
