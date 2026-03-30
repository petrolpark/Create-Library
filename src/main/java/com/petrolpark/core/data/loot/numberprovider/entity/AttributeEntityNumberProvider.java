package com.petrolpark.core.data.loot.numberprovider.entity;

import javax.annotation.ParametersAreNonnullByDefault;

import com.mojang.serialization.MapCodec;
import com.petrolpark.PetrolparkNumberProviderTypes;
import com.petrolpark.core.data.loot.numberprovider.NumberEstimate;
import com.petrolpark.util.CodecHelper;

import net.minecraft.core.Holder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.minecraft.world.level.storage.loot.LootContext;

/**
 * <p>{@code petrolpark:attribute}</p>
 * 
 * {@link LivingEntity#getAttributeValue(Holder) Get the value of an Attribute} of an Entity.
 * 
 * Arguments:
 * <ul>
 * <li>{@code attribute} - ID of {@link Attribute} to get
 * </ul>
 * 
 * @author petrolpark
 */
@ParametersAreNonnullByDefault
public record AttributeEntityNumberProvider(Holder<Attribute> attribute) implements EntityNumberProvider {

    public static final MapCodec<AttributeEntityNumberProvider> CODEC = CodecHelper.singleFieldMap(Attribute.CODEC, "attribute", AttributeEntityNumberProvider::attribute, AttributeEntityNumberProvider::new);

    @Override
    public float getFloat(Entity entity, LootContext lootContext) {
        return entity instanceof LivingEntity livingEntity
            ? (float)livingEntity.getAttributeValue(attribute())
            : 0f;
    };

    @Override
    public NumberEstimate getEstimate() {
        return attribute().value() instanceof RangedAttribute rangedAttribute ? NumberEstimate.ranged((float)rangedAttribute.getMinValue(), (float)rangedAttribute.getMaxValue()) : NumberEstimate.UNKNOWN;
    };

    @Override
    public LootEntityNumberProviderType getEntityNumberProviderType() {
        return PetrolparkNumberProviderTypes.ATTRIBUTE.get();
    };
    
};
