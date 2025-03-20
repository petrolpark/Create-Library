package com.petrolpark.core.data.loot.numberprovider.entity;

import java.util.Set;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.petrolpark.PetrolparkNumberProviderTypes;
import com.petrolpark.core.data.loot.numberprovider.itemstack.ItemStackNumberProvider;

import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;

public record EquipmentNumberProvider(EquipmentSlot slot, ItemStackNumberProvider value) implements EntityNumberProvider {

    public static final MapCodec<EquipmentNumberProvider> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        StringRepresentable.fromEnum(EquipmentSlot::values).optionalFieldOf("slot", EquipmentSlot.MAINHAND).forGetter(EquipmentNumberProvider::slot),
        ItemStackNumberProvider.CODEC.fieldOf("value").forGetter(EquipmentNumberProvider::value)
    ).apply(instance, EquipmentNumberProvider::new));

    @Override
    public float getFloat(Entity entity, LootContext lootContext) {
        if (entity instanceof LivingEntity livingEntity) return value.getFloat(livingEntity.getItemBySlot(slot), lootContext);
        return 0f;
    };

    @Override
    public Set<LootContextParam<?>> getReferencedContextParams() {
        return value.getReferencedContextParams();
    };

    @Override
    public LootEntityNumberProviderType getType() {
        return PetrolparkNumberProviderTypes.EQUIPMENT.get();
    };
    
};
