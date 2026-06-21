package petrolpark.mc.library.core.world.item;

import javax.annotation.Nonnull;

import petrolpark.mc.library.config.PetrolparkConfigs;
import petrolpark.mc.library.core.world.fluid.VirtualFluidWithContainer;
import com.tterrag.registrate.util.nullness.NonNullSupplier;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.ICapabilityProvider;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;

@EventBusSubscriber
public class FluidContainerItem extends Item {

    protected final NonNullSupplier<Fluid> fluid;

    protected final int containerVolume;
    protected final ItemLike emptyContainer;

    public static final FluidContainerItem bottle(NonNullSupplier<Fluid> fluid, Item.Properties properties) {
        return new FluidContainerItem(fluid, 250, Items.GLASS_BOTTLE, properties);
    };
    
    public FluidContainerItem(NonNullSupplier<Fluid> fluid, int containerVolume, ItemLike emptyContainer, Item.Properties properties) {
        super(properties);

        this.fluid = fluid;
        this.containerVolume = containerVolume;
        this.emptyContainer = emptyContainer;
    };

    public ICapabilityProvider<ItemStack, Void, IFluidHandlerItem> provideCapability() {
        return (stack, v) -> new FluidContainerItem.Handler(stack);
    };

    @Override
    public ItemStack getCraftingRemainingItem(@Nonnull ItemStack itemStack) {
        return new ItemStack(emptyContainer);
    };

    public class Handler implements IFluidHandlerItem {

        protected ItemStack container;

        public Handler(ItemStack container) {
            this.container = container;
        };

        @Override
        public int getTanks() {
            return 1;
        };

        public FluidStack getFluid() {
            return new FluidStack(fluid.get(), containerVolume);
        };

        @Override
        public FluidStack getFluidInTank(int tank) {
            return getFluid();
        };

        @Override
        public int getTankCapacity(int tank) {
            return containerVolume;
        };

        @Override
        public boolean isFluidValid(int tank, @Nonnull FluidStack stack) {
            return stack.is(fluid.get());
        };

        @Override
        public int fill(@Nonnull FluidStack resource, @Nonnull FluidAction action) {
            return 0; // This handler is for already-filled Bottles
        };

        @Override
        public FluidStack drain(@Nonnull FluidStack resource, @Nonnull FluidAction action) {
            if (container.getCount() != 1 || resource.getAmount() < containerVolume) return FluidStack.EMPTY;
            final FluidStack stack = getFluid();
            if (!FluidStack.isSameFluidSameComponents(resource, stack)) return FluidStack.EMPTY;
            if (action.execute()) container = new ItemStack(emptyContainer);
            return stack;
        };

        @Override
        public FluidStack drain(int maxDrain, @Nonnull FluidAction action) {
            if (container.getCount() != 1 || maxDrain < containerVolume) return FluidStack.EMPTY;
            if (action.execute()) container = new ItemStack(emptyContainer);
            return getFluid();
        };

        @Override
        public ItemStack getContainer() {
            return container;
        };

    };

    public static class EmptyHandler implements IFluidHandlerItem {

        protected ItemStack container;

        public EmptyHandler(ItemStack container, Void v) {
            this(container);
        };

        public EmptyHandler(ItemStack container) {
            this.container = container;
        };

        @Override
        public int getTanks() {
            return 1;
        };

        @Override
        public FluidStack getFluidInTank(int tank) {
            return FluidStack.EMPTY;
        };

        @Override
        public int getTankCapacity(int tank) {
            return Integer.MAX_VALUE;
        };

        @Override
        public boolean isFluidValid(int tank, @Nonnull FluidStack stack) {
            return stack.getFluid() instanceof VirtualFluidWithContainer containerFluid && containerFluid.containerItem.asItem() instanceof FluidContainerItem filledItem && filledItem.emptyContainer.equals(container.getItem());
        };

        @Override
        public int fill(@Nonnull FluidStack resource, @Nonnull FluidAction action) {
            if (container.getCount() != 1 || !(resource.getFluid() instanceof VirtualFluidWithContainer containerFluid && containerFluid.containerItem.asItem() instanceof FluidContainerItem filledItem) || resource.getAmount() < filledItem.containerVolume) return 0;
            if (action.execute()) container = new ItemStack(containerFluid.containerItem);
            return filledItem.containerVolume;
        };

        @Override
        public FluidStack drain(@Nonnull FluidStack resource, @Nonnull FluidAction action) {
            return FluidStack.EMPTY;
        };

        @Override
        public FluidStack drain(int maxDrain, @Nonnull FluidAction action) {
            return FluidStack.EMPTY;
        };

        @Override
        public ItemStack getContainer() {
            return container;
        };

    };

    @SubscribeEvent
    public static final void onRegisterCapabilities(RegisterCapabilitiesEvent event) {
        if (PetrolparkConfigs.common().glassBottleFluidCapability.get()) event.registerItem(Capabilities.FluidHandler.ITEM, FluidContainerItem.EmptyHandler::new, Items.GLASS_BOTTLE);
    };
};
