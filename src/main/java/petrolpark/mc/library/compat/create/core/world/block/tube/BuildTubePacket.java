package petrolpark.mc.library.compat.create.core.world.block.tube;

import net.createmod.catnip.net.base.ServerboundPacketPayload;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.items.wrapper.InvWrapper;
import petrolpark.mc.library.compat.create.RequiresCreate;
import petrolpark.mc.library.compat.create.registry.PetrolparkCreatePackets;
import petrolpark.mc.library.experimental.actionrecord.packet.recordable.AlwaysEnterRecordablePacketPayload;
import petrolpark.mc.library.util.ItemHelper;

@RequiresCreate
public class BuildTubePacket implements ServerboundPacketPayload, AlwaysEnterRecordablePacketPayload {

    public static final StreamCodec<RegistryFriendlyByteBuf, BuildTubePacket> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.registry(Registries.BLOCK), BuildTubePacket::getBlock,
        TubeSpline.Provider.STREAM_CODEC, BuildTubePacket::getSplineProvider,
        BuildTubePacket::new
    );

    public final Block block;
    public final ITubeBlock tubeBlock;
    public final TubeSpline spline;

    public BuildTubePacket(Block block, TubeSpline.Provider splineProvider) {
        this.block = block;
        if (block instanceof ITubeBlock tubeBlock) this.tubeBlock = tubeBlock; else throw new IllegalArgumentException(block.toString()+" is not a Tube Block");
        this.spline = splineProvider.provide(tubeBlock.getTubeMaxAngle(), tubeBlock.getTubeSegmentLength(), tubeBlock.getTubeSegmentRadius());
    };

    public BuildTubePacket(ITubeBlock tubeBlock, TubeSpline spline) {
        this.tubeBlock = tubeBlock;
        if (tubeBlock instanceof Block block) this.block = block; else throw new IllegalArgumentException("That is not a block");
        this.spline = spline;
    };

    public Block getBlock() {
        return block;
    };

    public TubeSpline.Provider getSplineProvider() {
        return spline.getProvider();
    };

    @Override
    public void handle(ServerPlayer player) {
        if (tubeBlock == null) return;
        spline.validate(player.level(), player, block.asItem(), tubeBlock);
        if (spline.getResult().success) {
            if (!player.getAbilities().instabuild) ItemHelper.removeItems(new InvWrapper(player.getInventory()), s -> s.is(block.asItem()), tubeBlock.getItemsForTubeLength(spline.getLength())); // Remove required Items
            tubeBlock.connectTube(player.level(), spline);
        };
    }

    @Override
    public PacketTypeProvider getTypeProvider() {
        return PetrolparkCreatePackets.BUILD_TUBE;
    };

    @Override
    public Component getDescription(ServerLevel level) {
        return translate(spline.start.getPos().toShortString(), spline.end.getPos().toShortString());
    };
    
};
