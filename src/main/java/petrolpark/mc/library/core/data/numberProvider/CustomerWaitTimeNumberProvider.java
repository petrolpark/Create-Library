package petrolpark.mc.library.core.data.numberProvider;

import java.util.Collections;
import java.util.Set;

import javax.annotation.Nonnull;

import petrolpark.mc.library.core.world.item.restaurant.customer.ICustomer;
import petrolpark.mc.library.registry.PetrolparkLootContextParams;
import petrolpark.mc.library.registry.PetrolparkNumberProviderTypes;

import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.providers.number.LootNumberProviderType;

/**
 * <p>{@code petrolpark:customer_wait_time}</p>
 * 
 * Get the amount of time the {@link ICustomer} {@link PetrolparkLootContextParams#CUSTOMER provided in} the {@link LootContext} has been waiting for their order, or {@code 1} if they are not a waiting {@link ICustomer}. No arguments.
 * 
 * @author petrolpark
 */
public class CustomerWaitTimeNumberProvider implements IEstimableNumberProvider {

    @Override
    public float getFloat(@Nonnull LootContext context) {
        ICustomer customer = context.getParam(PetrolparkLootContextParams.CUSTOMER);
        if (customer == null) return 1f;
        if (customer.getOrderTime() == ICustomer.INFINITE_ORDER_TIME) return 1f;
        return 1f - ((float)customer.getElapsedOrderTime() / (float)customer.getOrderTime());
    };

    @Override
    public NumberEstimate getEstimate() {
        return NumberEstimate.UNKNOWN;
    };

    @Override
    public float getMaxFloat(LootContext context) {
        return Float.MAX_VALUE;
    };

    @Override
    public LootNumberProviderType getType() {
        return PetrolparkNumberProviderTypes.CUSTOMER_WAIT_TIME.get();
    };

    @Override
    public Set<LootContextParam<?>> getReferencedContextParams() {
        return Collections.singleton(PetrolparkLootContextParams.CUSTOMER);
    };
    
};
