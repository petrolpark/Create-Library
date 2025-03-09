package com.petrolpark.contamination;

import java.util.ArrayList;
import java.util.Collections;

import com.petrolpark.PetrolparkDataComponents;
import com.petrolpark.PetrolparkRegistries;

import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.neoforged.neoforge.common.MutableDataComponentHolder;

public abstract class ComponentHolderContamination<OBJECT, OBJECT_STACK extends MutableDataComponentHolder> extends Contamination<OBJECT, OBJECT_STACK> {

    protected ComponentHolderContamination(OBJECT_STACK stack) {
        super(stack);
        orphanContaminants.addAll(stack.getOrDefault(PetrolparkDataComponents.ORPHAN_CONTAMINANTS, new ArrayList<Holder<Contaminant>>()).stream().map(Holder::value).toList());
        for (Contaminant contaminant : orphanContaminants) {
            contaminants.add(contaminant);
            contaminants.addAll(contaminant.getChildren());
        };
    };

    @Override
    public void save(RegistryAccess registries) {
        stack.set(PetrolparkDataComponents.ORPHAN_CONTAMINANTS, registries.registry(PetrolparkRegistries.Keys.CONTAMINANT).map(registry -> 
            orphanContaminants.stream().map(registry::wrapAsHolder).toList()
        ).orElse(Collections.emptyList()));
    };
    
};
