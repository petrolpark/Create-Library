package com.petrolpark.compat.create;

import java.util.Optional;

import javax.annotation.ParametersAreNonnullByDefault;

import com.petrolpark.Petrolpark;
import com.petrolpark.compat.create.core.AbstractPetrolparkCreateRegistrate;
import com.tterrag.registrate.providers.ProviderType;
import com.tterrag.registrate.providers.RegistrateProvider;
import com.tterrag.registrate.util.nullness.NonNullConsumer;

import net.neoforged.neoforge.data.event.GatherDataEvent;

@ParametersAreNonnullByDefault
public final class PetrolparkCreateRegistrate extends AbstractPetrolparkCreateRegistrate<PetrolparkCreateRegistrate> {

    protected PetrolparkCreateRegistrate() {
        super(Petrolpark.MOD_ID);
    };

    @Override
    protected void onData(GatherDataEvent event) {
        //NOOP, we use the non-Create registrate
    };

    @Override
    public <T extends RegistrateProvider> PetrolparkCreateRegistrate addDataGenerator(ProviderType<? extends T> type, NonNullConsumer<? extends T> cons) {
        Petrolpark.REGISTRATE.addDataGenerator(type, cons);
        return this;
    };
    
    @Override
    public <P extends RegistrateProvider> Optional<P> getDataProvider(ProviderType<P> type) {
        return Petrolpark.REGISTRATE.getDataProvider(type);
    };
    
};
