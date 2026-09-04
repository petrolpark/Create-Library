package petrolpark.mc.library.core.world.restaurant.gui;

import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import petrolpark.mc.library.core.world.restaurant.customer.ICustomer;
import petrolpark.mc.library.registry.PetrolparkMenuTypes;

@ParametersAreNonnullByDefault
public class RestaurantOrderMenu extends AbstractContainerMenu {

    public static final int SERVING_SLOT_X = 168;
    public static final int SERVING_SLOT_Y = 10;

    public final ICustomer customer;
    public final boolean canServe;
    public final SimpleContainer servingContainer = new SimpleContainer(1);

    public static RestaurantOrderMenu create(int containerId, Inventory playerInventory, ICustomer customer, boolean canServe) {
        return new RestaurantOrderMenu(PetrolparkMenuTypes.RESTAURANT_ORDER.get(), containerId, playerInventory, customer, canServe);
    };

    public RestaurantOrderMenu(MenuType<RestaurantOrderMenu> menuType, int containerId, Inventory playerInventory, RegistryFriendlyByteBuf buffer) {
        this(menuType, containerId, playerInventory, ICustomer.Provider.STREAM_CODEC.decode(buffer).provideCustomer(playerInventory.player.level()), buffer.readBoolean());
    };

    protected RestaurantOrderMenu(MenuType<RestaurantOrderMenu> menuType, int containerId, Inventory playerInventory, ICustomer customer, boolean canServe) {
        super(menuType, containerId);
        this.customer = customer;
        this.canServe = canServe;

        if (!canServe) return;

        // Serving slot
        addSlot(new Slot(servingContainer, 0, SERVING_SLOT_X, SERVING_SLOT_Y) {

            @Override
            public boolean mayPlace(ItemStack stack) {
                return customer.getOrder().ingredient().test(stack);
            };

            @Override
            public int getMaxStackSize() {
                return 1;
            };
        });

        // Inventory Slots
        final int inventoryX = 8;
        final int inventoryY = 84;
        for (int row = 0; row < 3; ++row)
			for (int col = 0; col < 9; ++col)
				addSlot(new Slot(playerInventory, col + row * 9 + 9, inventoryX + col * 18, inventoryY + row * 18));
		for (int hotbarSlot = 0; hotbarSlot < 9; ++hotbarSlot)
			addSlot(new Slot(playerInventory, hotbarSlot, inventoryX + hotbarSlot * 18, inventoryY + 58));
    };

    @Override
    public boolean stillValid(Player player) {
        return !canServe || customer.isNone() || customer.getPosition().distSqr(player.blockPosition()) < 16f;
    };

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack stack = ItemStack.EMPTY;
        final Slot slot = slots.get(index);
        if (slot == null || !slot.hasItem()) return stack;

        final ItemStack stackInSlot = slot.getItem();
        stack = stackInSlot.copy();
        if (index == 0) {
            if (!moveItemStackTo(stackInSlot, 1, 37, true))
                return ItemStack.EMPTY;
            slot.onQuickCraft(stackInSlot, stack);
        } else if (moveItemStackTo(stackInSlot, 0, 1, false)) {
            return ItemStack.EMPTY;
        } else if (index >= 1 && index < 28) {
            if (!moveItemStackTo(stackInSlot, 28, 37, false))
                return ItemStack.EMPTY;
        } else if (index >= 28 && index < 37) {
            if (!moveItemStackTo(stackInSlot, 1, 28, false))
                return ItemStack.EMPTY;
        } else if (!moveItemStackTo(stackInSlot, 1, 37, false)) {
            return ItemStack.EMPTY;
        };

        if (stackInSlot.isEmpty())
            slot.setByPlayer(ItemStack.EMPTY);
        else
            slot.setChanged();

        if (stackInSlot.getCount() == stack.getCount())
            return ItemStack.EMPTY;

        slot.onTake(player, stackInSlot);

        return stack;
    };

    @Override
    public void removed(Player player) {
        super.removed(player);
        clearContainer(player, servingContainer);
    };
    
};
