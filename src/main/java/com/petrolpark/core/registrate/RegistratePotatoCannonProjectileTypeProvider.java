package com.petrolpark.core.registrate;

import java.util.concurrent.CompletableFuture;

import com.petrolpark.RequiresCreate;
import com.petrolpark.compat.create.PetrolparkCreateRegistrateProviderTypes;
import com.simibubi.create.api.equipment.potatoCannon.PotatoCannonProjectileType;
import com.simibubi.create.api.registry.CreateRegistries;
import com.tterrag.registrate.AbstractRegistrate;
import com.tterrag.registrate.providers.ProviderType;

import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.PackOutput;

@RequiresCreate
public class RegistratePotatoCannonProjectileTypeProvider extends RegistrateDatapackBuiltinEntriesProvider<PotatoCannonProjectileType> {
    
    public RegistratePotatoCannonProjectileTypeProvider(AbstractRegistrate<?> parent, PackOutput output, CompletableFuture<Provider> registries) {
        super(parent, output, registries, CreateRegistries.POTATO_PROJECTILE_TYPE, new RegistrateDatapackBuiltinEntriesProvider.Bootstrap<>());
    };

    @Override
    public ProviderType<? extends RegistrateDatapackBuiltinEntriesProvider<PotatoCannonProjectileType>> getProviderType() {
        return PetrolparkCreateRegistrateProviderTypes.POTATO_CANNON_PROJECTILE;
    };
};
