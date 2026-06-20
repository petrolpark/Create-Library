package com.petrolpark.shared.world.item.shulkerbelt;

import javax.annotation.Nonnull;

import com.petrolpark.Petrolpark;
import com.petrolpark.registry.PetrolparkAttributes;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.Equipable;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;

// Temporary, for testing purposes
public class ShulkerBeltItem extends Item implements Equipable {

    public static final ItemAttributeModifiers ATTRIBUTE_MODIIFERS = ItemAttributeModifiers.builder()
        .add(PetrolparkAttributes.EXTRA_HOTBAR_SLOTS, new AttributeModifier(Petrolpark.asResource("shulker_belt_hotbar"), 3, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.ARMOR)
        .add(PetrolparkAttributes.EXTRA_INVENTORY_SIZE, new AttributeModifier(Petrolpark.asResource("shulker_belt_inventory"), 3, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.ARMOR)
        .build();

    public ShulkerBeltItem(Properties properties) {
        super(properties);
    };

    @Override
    public EquipmentSlot getEquipmentSlot() {
        return EquipmentSlot.LEGS;
    };

    @Override
    public ItemAttributeModifiers getDefaultAttributeModifiers(@Nonnull ItemStack stack) {
        return ATTRIBUTE_MODIIFERS;
    };
    
};
