package petrolpark.mc.library.core.data.numberProvider.entity;

import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.ValidationContext;
import petrolpark.mc.library.core.data.numberProvider.NumberEstimate;
import petrolpark.mc.library.registry.PetrolparkNumberProviderTypes;

@ParametersAreNonnullByDefault
public class AnimalMoodEntityNumberProvider implements EntityNumberProvider {

    public static final ResourceKey<LootEntityNumberProviderType> TYPE_KEY = PetrolparkNumberProviderTypes.ANIMAL_MOOD.getKey();

    @Override
    public float getFloat(Entity entity, LootContext lootContext) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getFloat'");
    };

    @Override
    public NumberEstimate getEstimate() {
        return NumberEstimate.POSITIVE;
    };

    @Override
    public LootEntityNumberProviderType getEntityNumberProviderType() {
        return PetrolparkNumberProviderTypes.ANIMAL_MOOD.get();
    };

    @Override
    public void validate(ValidationContext context) {
        EntityNumberProvider.super.validate(context);
        if (context.hasVisitedElement(TYPE_KEY)) context.reportProblem("Animal Mood Modifiers cannot contain the animal_mood EntityNumberProvider");
    };
    
};
