package com.petrolpark.mixin;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.petrolpark.core.contamination.GenericContamination;
import com.petrolpark.core.contamination.ItemContamination;
import com.petrolpark.core.world.block.entity.IShulkerBoxBlockEntityDuck;

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

    /**
     * Contaminate the dropped Shulker Box Item with the Contaminants of the placed Block Entity.
     * @param original
     * @param state
     * @param params
     */
    @ModifyReturnValue(
        method = "getDrops",
        at = @At("RETURN")
    )
    public List<ItemStack> modifyGetDrops(List<ItemStack> original, BlockState state, LootParams.Builder params) {
        BlockEntity be = params.getOptionalParameter(LootContextParams.BLOCK_ENTITY);
        if (be instanceof ShulkerBoxBlockEntity shulkerBox) {
            GenericContamination contamination = ((IShulkerBoxBlockEntityDuck)shulkerBox).getContamination();
            original.stream().filter(s -> s.getItem() instanceof BlockItem b && b.getBlock() == this).map(ItemContamination::get).forEach(contam -> contam.contaminateAll(contamination.streamOrphanExtrinsicContaminants()));
        };
        return original;
    };
    
    /**
     * Contaminate the placed Block Entity with the Contaminants of the Item.
     */
    @Override
    public void setPlacedBy(@Nonnull Level level, @Nonnull BlockPos pos, @Nonnull BlockState state, @Nullable LivingEntity placer, @Nonnull ItemStack stack) {
        level.getBlockEntity(pos, BlockEntityType.SHULKER_BOX)
            .map(IShulkerBoxBlockEntityDuck.class::cast)
            .ifPresent(duck -> duck.contaminateAll(ItemContamination.get(stack).streamOrphanExtrinsicContaminants()));
    };
    
};
