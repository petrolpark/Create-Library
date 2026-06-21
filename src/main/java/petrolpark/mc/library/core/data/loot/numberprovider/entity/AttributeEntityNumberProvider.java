package petrolpark.mc.library.core.data.loot.numberprovider.entity;

import javax.annotation.ParametersAreNonnullByDefault;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import petrolpark.mc.library.core.data.loot.numberprovider.NumberEstimate;
import petrolpark.mc.library.registry.PetrolparkNumberProviderTypes;

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
 * <li>{@code base} - Whether to get the base value (ignoring things like potion effects etc.) (defaults to {@code false})
 * </ul>
 * 
 * @author petrolpark
 */
@ParametersAreNonnullByDefault
public record AttributeEntityNumberProvider(Holder<Attribute> attribute, boolean baseValue) implements EntityNumberProvider {

    public static final MapCodec<AttributeEntityNumberProvider> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        Attribute.CODEC.fieldOf("attribute").forGetter(AttributeEntityNumberProvider::attribute),
        Codec.BOOL.optionalFieldOf("base", false).forGetter(AttributeEntityNumberProvider::baseValue)
    ).apply(instance, AttributeEntityNumberProvider::new));

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
