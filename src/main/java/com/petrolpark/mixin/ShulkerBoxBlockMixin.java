package com.petrolpark.mixin;

import java.util.List;

import javax.annotation.Nullable;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.petrolpark.block.entity.IShulkerBoxBlockEntityDuck;
import com.petrolpark.contamination.GenericContamination;
import com.petrolpark.contamination.ItemContamination;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

@Mixin(ShulkerBoxBlock.class)
public abstract class ShulkerBoxBlockMixin extends BaseEntityBlock {

    protected ShulkerBoxBlockMixin(Properties pProperties) {
        super(pProperties);
        throw new AssertionError();
    };

    @Inject(
        method = "getDrops",
        at = @At("RETURN"),
        cancellable = true
    )
    public void inGetDrops(BlockState pState, LootParams.Builder pParams, CallbackInfoReturnable<List<ItemStack>> cir) {
        List<ItemStack> drops = cir.getReturnValue();
        BlockEntity be = pParams.getOptionalParameter(LootContextParams.BLOCK_ENTITY);
        if (be instanceof ShulkerBoxBlockEntity shulkerBox) {
            GenericContamination contamination = ((IShulkerBoxBlockEntityDuck)shulkerBox).getContamination();
            drops.stream().filter(s -> s.getItem() instanceof BlockItem b && b.getBlock() == this).map(ItemContamination::get).forEach(contam -> contam.contaminateAll(pParams.getLevel().registryAccess(), contamination.streamOrphanExtrinsicContaminants()));
            cir.setReturnValue(drops);
        };
    };

    @Inject(
        method = "setPlacedBy",
        at = @At("HEAD")
    )
    public void inSetPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack, CallbackInfo ci) {
        level.getBlockEntity(pos, BlockEntityType.SHULKER_BOX)
            .map(IShulkerBoxBlockEntityDuck.class::cast)
            .ifPresent(duck -> duck.contaminateAll(level.registryAccess(), ItemContamination.get(stack).streamOrphanExtrinsicContaminants()));
    };
    
};
