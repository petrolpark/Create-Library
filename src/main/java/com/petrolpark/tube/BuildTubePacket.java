package com.petrolpark.tube;

import java.util.function.Supplier;

import com.petrolpark.RequiresCreate;
import com.petrolpark.network.packet.C2SPacket;
import com.petrolpark.util.ItemHelper;
import com.petrolpark.util.NetworkHelper;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.items.wrapper.InvWrapper;
import net.minecraftforge.network.NetworkEvent.Context;
import net.minecraftforge.registries.ForgeRegistries;

@RequiresCreate
public class BuildTubePacket extends C2SPacket {

    public final Block block;
    public final ITubeBlock tubeBlock;
    public final TubeSpline spline;

    public BuildTubePacket(ITubeBlock tubeBlock, TubeSpline spline) {
        this.tubeBlock = tubeBlock;
        if (tubeBlock instanceof Block block) this.block = block; else throw new IllegalArgumentException("That is not a block");
        this.spline = spline;
    };

    public BuildTubePacket(FriendlyByteBuf buffer) {
        block = buffer.readRegistryIdUnsafe(ForgeRegistries.BLOCKS);
        double segmentLength, segmentRadius, maxAngle;
        if (block instanceof ITubeBlock tubeBlock) {
            this.tubeBlock = tubeBlock;
            segmentLength = tubeBlock.getTubeSegmentLength();
            segmentRadius = tubeBlock.getTubeSegmentRadius();
            maxAngle = tubeBlock.getTubeMaxAngle();
        } else {
            this.tubeBlock = null;
            segmentLength = 1d;
            segmentRadius = 1d;
            maxAngle = 0d;
        };
        spline = new TubeSpline(NetworkHelper.readBlockFace(buffer), NetworkHelper.readBlockFace(buffer), NetworkHelper.readList(buffer, NetworkHelper::readVec3), maxAngle, segmentLength, segmentRadius);
    };

    @Override
    public void toBytes(FriendlyByteBuf buffer) {
        buffer.writeRegistryIdUnsafe(ForgeRegistries.BLOCKS, block);
        NetworkHelper.writeBlockFace(buffer, spline.start);
        NetworkHelper.writeBlockFace(buffer, spline.end);
        NetworkHelper.writeList(buffer, spline.getMiddleControlPoints(), NetworkHelper::writeVec3);
    };

    @Override
    public boolean handle(Supplier<Context> supplier) {
        Context context = supplier.get();
        ServerPlayer player = context.getSender();
        context.enqueueWork(() -> {
            if (tubeBlock == null) return;
            spline.validate(player.level(), player, block.asItem(), tubeBlock);
            if (spline.getResult().success) {
                if (!player.getAbilities().instabuild) ItemHelper.removeItems(new InvWrapper(player.getInventory()), s -> s.is(block.asItem()), tubeBlock.getItemsForTubeLength(spline.getLength())); // Remove required Items
                tubeBlock.connectTube(player.level(), spline);
            };
        });
        return true;
    };
    
};
