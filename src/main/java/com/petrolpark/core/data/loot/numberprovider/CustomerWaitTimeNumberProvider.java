package com.petrolpark.core.data.loot.numberprovider;

import java.util.Set;

import javax.annotation.Nonnull;

import java.util.Collections;

import com.petrolpark.PetrolparkLootContextParams;
import com.petrolpark.PetrolparkNumberProviderTypes;
import com.petrolpark.core.shop.customer.ICustomer;

import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.providers.number.LootNumberProviderType;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;

public class CustomerWaitTimeNumberProvider implements NumberProvider {

    @Override
    public float getFloat(@Nonnull LootContext context) {
        ICustomer customer = context.getParam(PetrolparkLootContextParams.CUSTOMER);
        if (customer == null) return 1f;
        if (customer.getOrderTime() == ICustomer.INFINITE_ORDER_TIME) return 1f;
        return 1f - ((float)customer.getElapsedOrderTime() / (float)customer.getOrderTime());
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
