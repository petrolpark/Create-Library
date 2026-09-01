package petrolpark.mc.library.compat.create.core.world.block.multiPart;

import org.jetbrains.annotations.Nullable;

import com.simibubi.create.api.schematic.requirement.SpecialBlockItemRequirement;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.content.schematics.requirement.ItemRequirement;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import petrolpark.mc.library.compat.create.core.world.block.multiPart.CreateMultiPartBlock.ICreatePart;
import petrolpark.mc.library.core.world.block.multiPart.MultiPartBlock;

public abstract class CreateMultiPartBlock<PART extends ICreatePart> extends MultiPartBlock<PART> implements SpecialBlockItemRequirement, IWrenchable {

    protected CreateMultiPartBlock(BlockBehaviour.Properties properties) {
        super(properties);
    };

    @Override
    public InteractionResult onSneakWrenched(BlockState state, UseOnContext context) {
        final Player player = context.getPlayer();
        if (player != null) {
            final PART part = clipperCache.get().get(state).clip(context.getClickedPos(), player);
            if (part != null && canSurviveWithout(state, context.getLevel(), context.getClickedPos(), player, true, context.getLevel().getFluidState(context.getClickedPos()), part)) {
                if (context.getLevel() instanceof ServerLevel serverLevel && !player.isCreative())
                    getPartDrops(part, state, serverLevel, context.getClickedPos(), serverLevel.getBlockEntity(context.getClickedPos()), player, context.getItemInHand())
                        .forEach(stack -> player.getInventory().placeItemBackInInventory(stack));
                switchBlockState(context.getLevel(), context.getClickedPos(), state, withoutPart(state, part));
                return InteractionResult.SUCCESS;
            };
        };
        return IWrenchable.super.onSneakWrenched(state, context);
    };

    @Override
    public ItemRequirement getRequiredItems(BlockState state, @Nullable BlockEntity blockEntity) {
        return getParts(state).stream().map(ICreatePart::itemRequirement).reduce(ItemRequirement.NONE, ItemRequirement::union); // Don't cache as it refers to raw (uninitialized) Items
    };

    public interface ICreatePart extends IPart {

        public ItemRequirement itemRequirement();
    };
};
