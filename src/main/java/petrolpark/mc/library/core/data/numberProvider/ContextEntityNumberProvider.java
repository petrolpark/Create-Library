package petrolpark.mc.library.core.data.numberProvider;

import javax.annotation.ParametersAreNonnullByDefault;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.providers.number.LootNumberProviderType;
import petrolpark.mc.library.core.data.IEntityTarget;
import petrolpark.mc.library.core.data.numberProvider.entity.EntityNumberProvider;
import petrolpark.mc.library.registry.PetrolparkNumberProviderTypes;

/**
 * <p>{@code petrolpark:entity_property}</p>
 * 
 * Get a {@link EntityNumberProvider} value of an Entity provided in the {@link LootContext}.
 * 
 * Arguments:
 * <ul>
 * <li> {@code target} - An {@link IEntityTarget} specifying which Entity in the {@link LootContext}
 * <li> {@code value} - An {@link EntityNumberProvider} to call on that Entity 
 * </ul>
 * 
 * @author petrolpark
 */
@ParametersAreNonnullByDefault
public record ContextEntityNumberProvider(IEntityTarget target, EntityNumberProvider value) implements IEstimableNumberProvider {

    public static final MapCodec<ContextEntityNumberProvider> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        IEntityTarget.STRICT_CODEC.fieldOf("target").forGetter(ContextEntityNumberProvider::target),
        EntityNumberProvider.CODEC.fieldOf("value").forGetter(ContextEntityNumberProvider::value)
    ).apply(instance, ContextEntityNumberProvider::new));

    @Override
    public float getFloat(LootContext context) {
        final Entity entity = target.get(context);
        if (entity != null) return value.getFloat(entity, context);
        return 0f;
    };

    @Override
    public int getInt(LootContext context) {
        final Entity entity = target.get(context);
        if (entity != null) return value.getInt(entity, context);
        return 0;
    };

    @Override
    public NumberEstimate getEstimate() {
        return value().getEstimate();
    };

    @Override
    public float getMaxFloat(LootContext context) {
        Entity entity = target.get(context);
        if (entity != null) return value.getMaxFloat(entity, context);
        return 0f;
    };

    @Override
    public LootNumberProviderType getType() {
        return PetrolparkNumberProviderTypes.CONTEXT_ENTITY.get();
    };

    @Override
    public void validate(ValidationContext context) {
        IEstimableNumberProvider.super.validate(context);
        value().validate(context.forChild(".entity_number_provider"));
    };
    
};
