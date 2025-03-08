package com.petrolpark.compat.create;

import com.petrolpark.Petrolpark;
import com.petrolpark.RequiresCreate;
import com.petrolpark.contamination.HasContaminantItemAttribute;
import com.simibubi.create.Create;
import com.simibubi.create.api.registry.CreateBuiltInRegistries;
import com.simibubi.create.content.logistics.item.filter.attribute.AllItemAttributeTypes;
import com.simibubi.create.content.logistics.item.filter.attribute.ItemAttribute;
import com.simibubi.create.content.logistics.item.filter.attribute.ItemAttributeType;
import net.minecraft.core.Registry;

@RequiresCreate
public class PetrolparkItemAttributes {
    
    public static final ItemAttributeType HAS_CONTAMINANT =
            Registry.register(CreateBuiltInRegistries.ITEM_ATTRIBUTE_TYPE, Petrolpark.asResource("has_contaminant"), new HasContaminantItemAttribute.Type());

    public static void register() {};
};
