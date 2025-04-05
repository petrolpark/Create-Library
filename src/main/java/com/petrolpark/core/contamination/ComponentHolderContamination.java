package com.petrolpark.core.contamination;

import java.util.ArrayList;

import com.petrolpark.PetrolparkDataComponents;

import net.minecraft.core.Holder;
import net.neoforged.neoforge.common.MutableDataComponentHolder;

public abstract class ComponentHolderContamination<OBJECT, OBJECT_STACK extends MutableDataComponentHolder> extends Contamination<OBJECT, OBJECT_STACK> {

    protected ComponentHolderContamination(OBJECT_STACK stack) {
        super(stack);
        orphanContaminants.addAll(stack.getOrDefault(PetrolparkDataComponents.ORPHAN_CONTAMINANTS, new ArrayList<Holder<Contaminant>>()).stream()
            .dropWhile(this::isIntrinsic)
            .toList()
        );
        for (Holder<Contaminant> contaminant : orphanContaminants) {
            contaminants.add(contaminant);
            contaminants.addAll(contaminant.value().getChildren());
        };
    };

    @Override
    public void save() {
        stack.set(PetrolparkDataComponents.ORPHAN_CONTAMINANTS, getOrphanHolderList());
    };
    
};
