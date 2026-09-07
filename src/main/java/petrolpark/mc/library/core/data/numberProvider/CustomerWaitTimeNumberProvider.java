package petrolpark.mc.library.core.data.numberProvider;

import java.util.Collections;
import java.util.Set;

import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.providers.number.LootNumberProviderType;
import petrolpark.mc.library.core.world.restaurant.customer.ICustomer;
import petrolpark.mc.library.registry.PetrolparkLootContextParams;
import petrolpark.mc.library.registry.PetrolparkNumberProviderTypes;

/**
 * <p>{@code petrolpark:customer_wait_time}</p>
 * 
 * Get the length of time the {@link ICustomer} {@link PetrolparkLootContextParams#CUSTOMER provided in} the {@link LootContext} has been waiting for their order, or {@code 0} if they are not a waiting {@link ICustomer}. No arguments.
 * 
 * @author petrolpark
 */
@ParametersAreNonnullByDefault
public class CustomerWaitTimeNumberProvider implements IEstimableNumberProvider {

    @Override
    public float getFloat(LootContext context) {
        return getInt(context);
    };

    @Override
    public int getInt(LootContext lootContext) {
        if (!lootContext.hasParam(PetrolparkLootContextParams.CUSTOMER)) return 0;
        final ICustomer customer = lootContext.getParam(PetrolparkLootContextParams.CUSTOMER);
        if (customer.isNone()) return 0;
        return (int)(lootContext.getLevel().getGameTime() - customer.getOrderTime());
    };

    @Override
    public NumberEstimate getEstimate() {
        return NumberEstimate.POSITIVE;
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
