package com.petrolpark;

import static com.petrolpark.Petrolpark.REGISTRATE;

import com.tterrag.registrate.util.entry.RegistryEntry;

import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;

public class PetrolparkAttributes {
    
    public static final RegistryEntry<Attribute, Attribute>

    EXTRA_HOTBAR_SLOTS = REGISTRATE.attribute("extra_hotbar_slots", s -> new RangedAttribute(s, 0d, 0d, 32d)
        .setSyncable(true)
        .setSentiment(Attribute.Sentiment.POSITIVE)
    ),

    EXTRA_INVENTORY_SIZE = REGISTRATE.attribute("extra_inventory_size", s -> new RangedAttribute(s, 0d, 0d, 64d)
        .setSyncable(true)
        .setSentiment(Attribute.Sentiment.POSITIVE)
    );

    public static final void register() {};
};
