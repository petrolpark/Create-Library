package com.petrolpark.compat.create;

import com.petrolpark.Petrolpark;
import com.petrolpark.RequiresCreate;
import com.petrolpark.compat.create.core.item.HasContaminantItemAttribute;
import com.simibubi.create.api.registry.CreateBuiltInRegistries;
import com.simibubi.create.content.logistics.item.filter.attribute.ItemAttributeType;

import net.minecraft.core.Registry;

@RequiresCreate
public class PetrolparkItemAttributeTypes {
    
    public static final ItemAttributeType HAS_CONTAMINANT =
            Registry.register(CreateBuiltInRegistries.ITEM_ATTRIBUTE_TYPE, Petrolpark.asResource("has_contaminant"), new HasContaminantItemAttribute.Type());

    public static final void init() {};
};
