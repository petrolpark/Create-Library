package petrolpark.mc.library.core.world.restaurant;

import java.util.List;

import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import petrolpark.mc.library.core.world.item.deletable.IDeletableItem;
import petrolpark.mc.library.core.world.restaurant.customer.ICustomer;
import petrolpark.mc.library.registry.PetrolparkDataComponentTypes;

@ParametersAreNonnullByDefault
public class RestaurantOrderItem extends Item implements IDeletableItem {

    public RestaurantOrderItem(Item.Properties properties) {
        super(properties);
    };

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        stack.update(PetrolparkDataComponentTypes.CUSTOMER_PROVIDER, ICustomer.none(), ICustomer.Provider.update(level)); // Update client in case the entity ID changed
        if (isSelected && entity instanceof Player player) stack.getOrDefault(PetrolparkDataComponentTypes.CUSTOMER_PROVIDER, ICustomer.none()).provideCustomer(level).tickWhileOrderItemHeld(stack, level, player, slotId);
    };

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        final Level level = context.level();
        if (level == null) return;
        final ICustomer customer = stack.getOrDefault(PetrolparkDataComponentTypes.CUSTOMER_PROVIDER, ICustomer.none()).provideCustomer(level);
        if (customer.isNone()) return;
        tooltipComponents.add(customer.getDescription());
    };
    
};
