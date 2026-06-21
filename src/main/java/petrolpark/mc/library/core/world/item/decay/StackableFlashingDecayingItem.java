package petrolpark.mc.library.core.world.item.decay;

import java.util.List;
import java.util.function.Consumer;

import javax.annotation.Nonnull;

import petrolpark.mc.library.compat.create.RequiresCreate;
import com.simibubi.create.foundation.item.render.SimpleCustomRenderer;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;

@RequiresCreate
public abstract class StackableFlashingDecayingItem extends Item {

    public StackableFlashingDecayingItem(Properties properties) {
        super(properties);
    };

    public boolean areDecayTimesCombineable(ItemStack stack1, ItemStack stack2) {
        return true;
    };

    @Override
    public void appendHoverText(@Nonnull ItemStack stack, @Nonnull Item.TooltipContext context, @Nonnull List<Component> tooltip, @Nonnull TooltipFlag pIsAdvanced) {
        super.appendHoverText(stack, context, tooltip, pIsAdvanced);
        
    };

    @Override
    public void onCraftedBy(@Nonnull ItemStack stack, @Nonnull Level level, @Nonnull Player player) {
        ItemDecay.startDecay(stack, 0);
    };

    @OnlyIn(Dist.CLIENT)
    @Override
    public void initializeClient(@Nonnull Consumer<IClientItemExtensions> consumer) {
        consumer.accept(SimpleCustomRenderer.create(this, new FlashingDecayingItemRenderer()));
    };
    
};
