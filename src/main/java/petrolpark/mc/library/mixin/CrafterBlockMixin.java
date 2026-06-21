package petrolpark.mc.library.mixin;

import java.util.Optional;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import petrolpark.mc.library.core.world.item.crafting.recipeBook.IBookRequiredRecipe;
import petrolpark.mc.library.core.world.item.crafting.recipeBook.IRecipeBookAcceptorBlock;
import petrolpark.mc.library.registry.PetrolparkRecipeTypes;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.CrafterBlock;
import net.minecraft.world.level.block.entity.CrafterBlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

@Mixin(CrafterBlock.class)
public abstract class CrafterBlockMixin extends BaseEntityBlock implements IRecipeBookAcceptorBlock {
    
    protected CrafterBlockMixin(BlockBehaviour.Properties properties) {
        super(properties);
        throw new AssertionError();
    };

    @ModifyExpressionValue(
        method = "dispenseFrom",
        at = @At(
            value = "INVOKE",
            target = "getPotentialResults"
        )
    )
    public Optional<RecipeHolder<CraftingRecipe>> petrolpark$addBookRequiredRecipes(Optional<RecipeHolder<CraftingRecipe>> original, BlockState state, ServerLevel level, BlockPos pos) {
        if (!(level.getBlockEntity(pos) instanceof CrafterBlockEntity crafter)) return original;
        return original.or(() -> level.getRecipeManager()
            .getRecipeFor(PetrolparkRecipeTypes.CRAFTING_BOOK_REQUIRED.get(), crafter.asCraftInput(), level)
            .filter(rh -> IBookRequiredRecipe.hasRequiredBook(level, pos, rh))
            .map(rh -> new RecipeHolder<>(rh.id(), (CraftingRecipe)rh.value()))
        );
    };

    @Override
    public boolean acceptsRecipeBook(Level level, BlockPos pos, BlockState state, RecipeHolder<?> recipeHolder) {
        return recipeHolder.value().getType() == PetrolparkRecipeTypes.CRAFTING_BOOK_REQUIRED.get();
    };
};
