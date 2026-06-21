package petrolpark.mc.library.mixin.compat.create;

import java.util.function.Consumer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import petrolpark.mc.library.compat.create.registry.PetrolparkCreateRecipeTypes;
import petrolpark.mc.library.core.world.item.crafting.recipeBook.IRecipeBookAcceptorBlockEntity;
import petrolpark.mc.library.registry.PetrolparkRecipeTypes;
import petrolpark.mc.library.util.RecipeHelper;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.crafter.MechanicalCrafterBlockEntity;
import com.simibubi.create.content.kinetics.crafter.MechanicalCraftingInput;
import com.simibubi.create.content.kinetics.crafter.RecipeGridHandler;
import com.simibubi.create.content.kinetics.crafter.RecipeGridHandler.GroupedItems;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

@Mixin(MechanicalCrafterBlockEntity.class)
public abstract class MechanicalCrafterBlockEntityMixin extends KineticBlockEntity implements IRecipeBookAcceptorBlockEntity {

    public MechanicalCrafterBlockEntityMixin(BlockEntityType<?> typeIn, BlockPos pos, BlockState state) {
        super(typeIn, pos, state);
        throw new AssertionError();
    };

    @WrapOperation(
        method = "Lcom/simibubi/create/content/kinetics/crafter/MechanicalCrafterBlockEntity;tick()V",
        at = @At(
            value = "INVOKE",
            target = "Lcom/simibubi/create/content/kinetics/crafter/RecipeGridHandler;tryToApplyRecipe(Lnet/minecraft/world/level/Level;Lcom/simibubi/create/content/kinetics/crafter/RecipeGridHandler$GroupedItems;)Lnet/minecraft/world/item/ItemStack;"
        ),
        remap = false
    )
    public ItemStack petrolpark$advancedRecipeChecks(Level world, GroupedItems items, Operation<ItemStack> original) {
        ItemStack result = original.call(world, items);
        if (result == null) {
            final MechanicalCraftingInput craftingInput = MechanicalCraftingInput.of(items);
            result = world.getRecipeManager().getRecipeFor(PetrolparkRecipeTypes.CRAFTING_BOOK_REQUIRED.get(), craftingInput, world)
                .or(() -> PetrolparkCreateRecipeTypes.RECIPE_BOOK_MECHANICAL_CRAFTING.find(craftingInput, world))
                .filter(rh -> RecipeHelper.isValidAt(rh, world, getBlockPos()))
                .map(rh -> rh.value().assemble(craftingInput, world.registryAccess()))
                .orElse(null);
        };
        return result;
    };

    @Override
    public void addProxyRecipeBookAcceptorPositions(Consumer<BlockPos> posAdder) {
        RecipeGridHandler.getAllCraftersOfChain((MechanicalCrafterBlockEntity)(Object)this).stream().map(BlockEntity::getBlockPos).forEach(posAdder);
    };

    @Override
    public boolean acceptsRecipeBook(RecipeHolder<?> recipeHolder) {
        return recipeHolder.value() instanceof CraftingRecipe;
    };
    
};
