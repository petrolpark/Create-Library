package com.petrolpark.core.data.loot.numberprovider.entity;

import java.util.Set;

import javax.annotation.ParametersAreNonnullByDefault;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.petrolpark.PetrolparkNumberProviderTypes;
import com.petrolpark.core.data.loot.numberprovider.ContextToolNumberProvider;
import com.petrolpark.core.data.loot.numberprovider.NumberEstimate;
import com.petrolpark.core.data.loot.numberprovider.itemstack.ItemStackNumberProvider;

import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;

/**
 * <p>{@code petrolpark:equipment}</p>
 * 
 * Get a {@link ItemStackNumberProvider value} of an equipped Item.
 * 
 * Arguments:
 * <ul>
 * <li> {@code slot} - Any {@link EquipmentSlot#getName() Equipment Slot name}
 * <li> {@code value} - An {@link ItemStackNumberProvider} to call on the equipped Item Stack
 * </ul>
 * 
 * @author petrolpark
 * 
 * @see ContextToolNumberProvider Getting the value from the mainhand Item directly from the LootContext
 */
@ParametersAreNonnullByDefault
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
    public float getMaxFloat(Entity entity, LootContext lootContext) {
        if (entity instanceof LivingEntity livingEntity) return value.getMaxFloat(livingEntity.getItemBySlot(slot), lootContext);
        return 0f;
    };

    @Override
    public NumberEstimate getEstimate() {
        return value().getEstimate();
    };

    @Override
    public Set<LootContextParam<?>> getReferencedContextParams() {
        return value.getReferencedContextParams();
    };

    @Override
    public LootEntityNumberProviderType getEntityNumberProviderType() {
        return PetrolparkNumberProviderTypes.EQUIPMENT.get();
    };
    
};
