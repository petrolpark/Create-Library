package petrolpark.mc.library.mixin.compat.farmersdelight;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import petrolpark.mc.library.shared.world.effect.CryingMobEffect;
import vectorwing.farmersdelight.common.block.entity.CuttingBoardBlockEntity;
import vectorwing.farmersdelight.common.block.entity.SyncedBlockEntity;
import vectorwing.farmersdelight.common.crafting.CuttingBoardRecipe;

@Mixin(CuttingBoardBlockEntity.class)
public abstract class CuttingBoardBlockEntityMixin extends SyncedBlockEntity {
    
    public CuttingBoardBlockEntityMixin(BlockEntityType<?> blockEntityType, BlockPos pos, BlockState state) {
        super(blockEntityType, pos, state);
        throw new AssertionError();
    };

    @Inject(
        method = "lambda$processStoredItemUsingTool$2",
        at = @At(
            value = "INVOKE",
            target = "playProcessingSound"
        )
    )
    public void petrolpark$causeCrying(ItemStack toolStack, Player player, RecipeHolder<CuttingBoardRecipe> matchingRecipe, CallbackInfo ci) {
        CryingMobEffect.applyInRange(getLevel(), getBlockPos());
    };
};
