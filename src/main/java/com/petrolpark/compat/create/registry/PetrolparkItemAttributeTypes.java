package com.petrolpark.compat.create.registry;

import com.petrolpark.Petrolpark;
import com.petrolpark.compat.create.RequiresCreate;
import com.petrolpark.compat.create.core.world.item.attribute.HasFlagItemAttribute;
import com.petrolpark.compat.create.core.world.item.attribute.IsCompressedItemAttribute;
import com.simibubi.create.api.registry.CreateBuiltInRegistries;
import com.simibubi.create.content.logistics.item.filter.attribute.ItemAttributeType;

import net.minecraft.core.Registry;

@RequiresCreate
public class PetrolparkItemAttributeTypes {
    
    public static final ItemAttributeType
    
    HAS_FLAG = Registry.register(CreateBuiltInRegistries.ITEM_ATTRIBUTE_TYPE, Petrolpark.asResource("has_flag"), new HasFlagItemAttribute.Type()),
    IS_COMPRESSED = Registry.register(CreateBuiltInRegistries.ITEM_ATTRIBUTE_TYPE, Petrolpark.asResource("is_compressed"), new IsCompressedItemAttribute.Type());

    public static final void init() {};
};
