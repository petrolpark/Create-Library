package com.petrolpark.compat.create;

import static com.petrolpark.Petrolpark.REGISTRATE;

import com.simibubi.create.content.processing.sequenced.SequencedAssemblyItem;
import com.tterrag.registrate.util.entry.ItemEntry;

public class PetrolparkCreateItems {
    
    public static final ItemEntry<SequencedAssemblyItem> UNPROCESSED_MASHED_POTATO = REGISTRATE.item("unprocessed_mashed_potato", SequencedAssemblyItem::new)
        .defaultModel()
        .register();

    public static final void register() {};
};
