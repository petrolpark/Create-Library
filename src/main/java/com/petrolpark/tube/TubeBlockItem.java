package com.petrolpark.tube;

import com.petrolpark.util.BlockFace;
import com.simibubi.create.foundation.utility.DistExecutor;

import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.neoforged.api.distmarker.Dist;

public class TubeBlockItem extends BlockItem {

    public final ITubeBlock tubeBlock;

    public <B extends Block & ITubeBlock> TubeBlockItem(B block, Properties properties) {
        super(block, properties);
        this.tubeBlock = block;
    };

    @Override
    public InteractionResult place(BlockPlaceContext context) {
        InteractionResult result = super.place(context);
        if (context.getLevel().isClientSide() && result == InteractionResult.SUCCESS) {
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
                ClientTubePlacementHandler.tryConnect(BlockFace.of(context.getClickedPos(), getConnectingFace(context)), context.getItemInHand(), tubeBlock, true);
            });
        };
        return result;
    };

    /**
     * Must match {@link ITubeBlock#getTubeConnectingFace(net.minecraft.world.level.Level, net.minecraft.core.BlockPos, net.minecraft.world.level.block.state.BlockState)}
     * @param context
     */
    public Direction getConnectingFace(BlockPlaceContext context) {
        return context.getClickedFace();
    };

    
};
