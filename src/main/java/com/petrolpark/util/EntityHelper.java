package com.petrolpark.util;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.item.ItemStack;

public class EntityHelper {
    
    public static final void refreshEquipmentAttributeModifiers(LivingEntity entity) {

        for (EquipmentSlot slot : EquipmentSlot.values()) {
            ItemStack newStack = entity.getItemBySlot(slot);
            if (!newStack.isEmpty()) {
                newStack.forEachModifier(slot, (attribute, attributeModifier) -> {
                    AttributeInstance attributeInstance = entity.getAttribute(attribute);
                    if (attributeInstance != null) {
                        attributeInstance.removeModifier(attributeModifier);
                        attributeInstance.addTransientModifier(attributeModifier);
                    };
                });
            };
        };
    };
};
