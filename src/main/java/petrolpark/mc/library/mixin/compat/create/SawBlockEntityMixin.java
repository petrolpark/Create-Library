package petrolpark.mc.library.mixin.compat.create;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import com.simibubi.create.content.kinetics.base.BlockBreakingKineticBlockEntity;
import com.simibubi.create.content.kinetics.saw.SawBlockEntity;
import com.simibubi.create.content.processing.recipe.ProcessingInventory;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import petrolpark.mc.library.PetrolparkTags;
import petrolpark.mc.library.config.PetrolparkConfigs;
import petrolpark.mc.library.core.flags.IFlagPole;
import petrolpark.mc.library.core.flags.ItemFlagPole;
import petrolpark.mc.library.core.world.item.decay.ItemDecay;
import petrolpark.mc.library.shared.world.effect.CryingMobEffect;

@Mixin(value = SawBlockEntity.class, remap = false)
public abstract class SawBlockEntityMixin extends BlockBreakingKineticBlockEntity {

    public SawBlockEntityMixin(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        throw new AssertionError();
    };

    @Shadow
    public ProcessingInventory inventory;

    @Inject(
        method = "applyRecipe()V",
        at = @At("TAIL"),
        locals = LocalCapture.CAPTURE_FAILSOFT,
        remap = false
    )
    public void petrolpark$propagateFlagsAndStartDecay(CallbackInfo ci, ItemStack input, List<? extends Recipe<?>> recipes) {
        if (recipes.isEmpty()) return;

        // Propagate Flags
        final IFlagPole<?, ?> inputFlags = ItemFlagPole.get(input);
        for (int slot = 0; slot < inventory.getSlots(); slot++) {
            ItemStack stack = inventory.getStackInSlot(slot);
            ItemDecay.startDecay(stack);
            Level level = getLevel();
            if (level != null && PetrolparkConfigs.server().createCuttingRecipesPropagateFlags.get()) ItemFlagPole.get(stack).flagAll(inputFlags.streamAllFlags());
        };

        // Make nearby entities cry
        if (PetrolparkTags.Items.CUTTING_CAUSES_CRYING.matches(input)) {
            CryingMobEffect.applyInRange(getLevel(), getBlockPos());
        };
    };
};
