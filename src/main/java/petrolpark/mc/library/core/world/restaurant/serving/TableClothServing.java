package petrolpark.mc.library.core.world.restaurant.serving;

import java.util.Objects;
import java.util.Optional;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import javax.annotation.Nullable;

import com.simibubi.create.content.logistics.tableCloth.TableClothBlockEntity;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import petrolpark.mc.library.core.world.restaurant.customer.ICustomer;
import petrolpark.mc.library.registry.PetrolparkDataComponentTypes;

public record TableClothServing(TableClothBlockEntity tableCloth, ICustomer customer, int orderStackIndex, ItemStack expectedOrderStack, int servingStackIndex, ItemStack expectedServingStack) implements IServingBlockEntity.Serving.Satisfied {

    public static Stream<? extends IServingBlockEntity.Serving> stream(TableClothBlockEntity tableCloth) {
        if (tableCloth.isShop() || tableCloth.manuallyAddedItems.size() == 0) return Stream.empty();
        final Level level = tableCloth.getLevel();
        if (level == null) return Stream.empty();

        return IntStream.range(0, tableCloth.manuallyAddedItems.size()).mapToObj(orderIndex -> {
            final ItemStack orderStack = tableCloth.manuallyAddedItems.get(orderIndex);
            final ICustomer customer = orderStack.getOrDefault(PetrolparkDataComponentTypes.CUSTOMER_PROVIDER, ICustomer.none()).provideCustomer(level);
            if (customer.isNone()) return null;
            for (int servingIndex = 0; servingIndex < tableCloth.manuallyAddedItems.size(); servingIndex++) {
                if (servingIndex == orderIndex) continue;
                final ItemStack servingStack = tableCloth.manuallyAddedItems.get(servingIndex);
                if (customer.getOrder().ingredient().test(servingStack))
                    return new TableClothServing(tableCloth, customer, servingIndex, orderIndex);
            };
            return new IServingBlockEntity.Serving.Unsatisfied(customer);
        }).filter(Objects::nonNull);
    };

    @Nullable
    public static IServingBlockEntity.Serving serve(TableClothBlockEntity tableCloth, boolean simulate, ItemStack stack) {
        if (tableCloth.isShop() || tableCloth.manuallyAddedItems.size() >= 4) return null;
        final Level level = tableCloth.getLevel();
        if (level == null) return null;

        for (int orderIndex = 0; orderIndex < tableCloth.manuallyAddedItems.size(); orderIndex++) {
            final ICustomer customer = tableCloth.manuallyAddedItems.get(orderIndex).getOrDefault(PetrolparkDataComponentTypes.CUSTOMER_PROVIDER, ICustomer.none()).provideCustomer(level);
            if (customer.isNone()) continue;
            if (customer.getOrder().ingredient().test(stack)) {
                if (!simulate) {
                    tableCloth.manuallyAddedItems.add(stack);
                    tableCloth.notifyUpdate();
                };
                return new TableClothServing(tableCloth, customer, orderIndex, tableCloth.manuallyAddedItems.size() - 1);
            };
        };

        return null;
    };

    TableClothServing(TableClothBlockEntity tableCloth, ICustomer customer, int orderStackIndex, int servingStackIndex) {
        this(tableCloth, customer, orderStackIndex, tableCloth.manuallyAddedItems.get(orderStackIndex).copy(), servingStackIndex, tableCloth.manuallyAddedItems.get(servingStackIndex).copy());
    };

    @Override
    public boolean isStillSatisfied() {
        return orderStackIndex() < tableCloth.manuallyAddedItems.size()
            && servingStackIndex() < tableCloth.manuallyAddedItems.size()
            && tableCloth.manuallyAddedItems.get(orderStackIndex()).equals(expectedOrderStack())
            && tableCloth.manuallyAddedItems.get(servingStackIndex()).equals(expectedServingStack());
    };

    @Override
    public ItemStack serving() {
        return expectedServingStack();
    };

    @Override
    public boolean eat(int ticksElapsed, float eatingProgress) {
        if (eatingProgress != 1f) return false;

        final Level level = tableCloth().getLevel();
        if (level == null) return true;
 
        tableCloth().manuallyAddedItems.set(orderStackIndex(), ItemStack.EMPTY); // Remove the order item
        tableCloth().manuallyAddedItems.set(servingStackIndex(), ItemStack.EMPTY); // Remove the ordered item itself

        Optional.ofNullable(serving().get(DataComponents.FOOD)).flatMap(FoodProperties::usingConvertsTo)
            .map(ItemStack::copy)
            .ifPresent(tableCloth().manuallyAddedItems::add);

        tableCloth.manuallyAddedItems.removeIf(ItemStack::isEmpty);

        tableCloth().notifyUpdate();
        return true;
    };
    
};
