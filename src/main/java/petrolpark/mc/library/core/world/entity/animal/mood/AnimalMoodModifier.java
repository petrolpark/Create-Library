package petrolpark.mc.library.core.world.entity.animal.mood;

import java.util.Collections;
import java.util.List;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.core.Holder;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.level.storage.loot.ValidationContext;
import petrolpark.mc.library.core.data.numberProvider.entity.AnimalMoodEntityNumberProvider;
import petrolpark.mc.library.core.data.numberProvider.entity.EntityNumberProvider;
import petrolpark.mc.library.core.data.stringProvider.StringProvider;
import petrolpark.mc.library.registry.PetrolparkLootContextParamSets;
import petrolpark.mc.library.registry.PetrolparkRegistries;

public record AnimalMoodModifier(
    EntityPredicate predicate,
    EntityNumberProvider value,
    String translationKey,
    List<StringProvider> translationArgs
) {
    
    public static final Codec<AnimalMoodModifier> UNVALIDATED_DIRECT_CODEC = RecordCodecBuilder.create(instance -> 
        instance.group(
            EntityPredicate.CODEC.fieldOf("predicate").forGetter(AnimalMoodModifier::predicate),
            EntityNumberProvider.CODEC.fieldOf("value").forGetter(AnimalMoodModifier::value),
            Codec.STRING.fieldOf("translation_key").forGetter(AnimalMoodModifier::translationKey),
            StringProvider.CODEC.listOf().optionalFieldOf("translation_arguments", Collections.emptyList()).forGetter(AnimalMoodModifier::translationArgs)
        ).apply(instance, AnimalMoodModifier::new)
    );

    public static final Codec<AnimalMoodModifier> DIRECT_CODEC = UNVALIDATED_DIRECT_CODEC.validate(modifier -> {
        final ProblemReporter.Collector problemReporterCollector = new ProblemReporter.Collector();
        final ValidationContext validationContext = new ValidationContext(problemReporterCollector, PetrolparkLootContextParamSets.ANIMAL_HAPPINESS);
        modifier.value().validate(validationContext.enterElement("animal_mood_modifier", AnimalMoodEntityNumberProvider.TYPE_KEY));
        return problemReporterCollector.getReport()
            .map(error -> DataResult.<AnimalMoodModifier>error(() -> "Validation error in Animal Mood Modifier: " + error))
            .orElseGet(() -> DataResult.success(modifier));
    });

    public static final Codec<Holder<AnimalMoodModifier>> CODEC = RegistryFileCodec.create(PetrolparkRegistries.Keys.ANIMAL_MOOD_MODIFIER, DIRECT_CODEC);
        
};
