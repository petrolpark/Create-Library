package com.petrolpark.shop.customer;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.petrolpark.PetrolparkLootContextParams;

import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraftforge.common.capabilities.AutoRegisterCapability;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.util.LazyOptional;
import net.neoforged.neoforge.capabilities.ICapabilityProvider;

@AutoRegisterCapability
public class EntityCustomer extends AbstractCustomer implements ICapabilityProvider {

    public static final Capability<EntityCustomer> CAPABILITY = CapabilityManager.get(new CapabilityToken<>(){});

    public final Entity entity;

    public EntityCustomer(Entity entity) {
        this.entity = entity;
    };

    @Override
    public Component getName() {
        return entity.getDisplayName();
    };

    @Override
    public void supplyLootParams(LootParams.Builder builder) {
        builder.withParameter(PetrolparkLootContextParams.CUSTOMER_ENTITY, entity);
    };

    @Override
    public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == CAPABILITY) return LazyOptional.of(() -> this).cast();
        return LazyOptional.empty();
    };
    
};
