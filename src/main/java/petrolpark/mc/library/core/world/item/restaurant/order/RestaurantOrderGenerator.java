package petrolpark.mc.library.core.world.item.restaurant.order;

import java.util.Collections;
import java.util.List;

import javax.annotation.ParametersAreNonnullByDefault;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.Holder;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootContextUser;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.NumberProviders;
import petrolpark.mc.library.core.data.recipe.ingredient.randomizer.IngredientRandomizer;
import petrolpark.mc.library.core.data.reward.generator.IRewardGenerator;
import petrolpark.mc.library.registry.PetrolparkLootContextParamSets;
import petrolpark.mc.library.util.DataValidationHelper;

@ParametersAreNonnullByDefault
public record RestaurantOrderGenerator(
    //NumberProvider time,
    IngredientRandomizer order,
    List<RestaurantOrderGenerator.ModifierEntry> modifiers,
    List<RestaurantOrderGenerator.RewardsEntry> rewards
) implements LootContextUser {

    public static final Codec<RestaurantOrderGenerator> UNVALIDATED_DIRECT_CODEC = Codec.lazyInitialized(() -> RecordCodecBuilder.create(instance -> 
        instance.group(
            //NumberProviders.CODEC.optionalFieldOf("time", ConstantValue.exactly(-1)).forGetter(RestaurantOrderGenerator::time),
            IngredientRandomizer.CODEC.fieldOf("order").forGetter(RestaurantOrderGenerator::order),
            Codec.list(RestaurantOrderGenerator.ModifierEntry.CODEC).optionalFieldOf("modifiers", Collections.emptyList()).forGetter(RestaurantOrderGenerator::modifiers),
            Codec.list(RestaurantOrderGenerator.RewardsEntry.CODEC).fieldOf("reward").forGetter(RestaurantOrderGenerator::rewards)
        ).apply(instance, RestaurantOrderGenerator::new)
    ));

    public static final Codec<RestaurantOrderGenerator> DIRECT_CODEC = UNVALIDATED_DIRECT_CODEC.validate(generator -> {
        final ProblemReporter.Collector problemReporterCollector = new ProblemReporter.Collector();
        generator.validate(new ValidationContext(problemReporterCollector, PetrolparkLootContextParamSets.RESTAURANT_ORDER_GENERATION));
        return problemReporterCollector.getReport()
            .map(error -> DataResult.<RestaurantOrderGenerator>error(() -> "Validation error in Restaurant Order Generator: " + error))
            .orElseGet(() -> DataResult.success(generator));
    });

    public RestaurantOrder generate(LootContext context, int id) {
        return new RestaurantOrder(
            id,
            order().generate(context),
            modifiers().stream()
                .filter(entry -> context.getRandom().nextFloat() < entry.chance().getFloat(context))
                .map(entry -> new RestaurantOrderModifier(
                    entry.ingredient().generate(context),
                    entry.successMultiplier(), entry.failureMultiplier(),
                    entry.visibility(), entry.persistsToMenu()
                ))
                .toList(),
            rewards().stream()
                .filter(entry -> context.getRandom().nextFloat() < entry.chance().getFloat(context))
                .flatMap(entry -> 
                    entry.rewards().value()
                        .generate(context)
                        .map(reward -> new RestaurantOrder.RewardEntry(reward, entry.visibility(), entry.persistsToMenu()))
                ).toList()
        );
    };

    public record ModifierEntry(NumberProvider chance, IngredientRandomizer ingredient, NumberProvider successMultiplier, NumberProvider failureMultiplier, IRestaurantOrderThing.Visibility visibility, boolean persistsToMenu) implements IRestaurantOrderThing {

        public static final Codec<RestaurantOrderGenerator.ModifierEntry> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                NumberProviders.CODEC.optionalFieldOf("chance", ConstantValue.exactly(1f)).forGetter(RestaurantOrderGenerator.ModifierEntry::chance),
                IngredientRandomizer.CODEC.fieldOf("modifier").forGetter(RestaurantOrderGenerator.ModifierEntry::ingredient),
                NumberProviders.CODEC.fieldOf("success").forGetter(RestaurantOrderGenerator.ModifierEntry::successMultiplier),
                NumberProviders.CODEC.optionalFieldOf("failure", ConstantValue.exactly(0f)).forGetter(RestaurantOrderGenerator.ModifierEntry::failureMultiplier)
            ).and(IRestaurantOrderThing.commonFields(instance))
            .apply(instance, RestaurantOrderGenerator.ModifierEntry::new)
        );
    };

    public record RewardsEntry(NumberProvider chance, Holder<IRewardGenerator> rewards, IRestaurantOrderThing.Visibility visibility, boolean persistsToMenu) implements IRestaurantOrderThing {

        public static final Codec<RestaurantOrderGenerator.RewardsEntry> CODEC = RecordCodecBuilder.create(instance -> 
            instance.group(
                NumberProviders.CODEC.optionalFieldOf("chance", ConstantValue.exactly(1f)).forGetter(RestaurantOrderGenerator.RewardsEntry::chance),
                IRewardGenerator.CODEC.fieldOf("rewards").forGetter(RestaurantOrderGenerator.RewardsEntry::rewards)
            ).and(IRestaurantOrderThing.commonFields(instance))
            .apply(instance, RestaurantOrderGenerator.RewardsEntry::new)
        );
    };

    @Override
    public void validate(ValidationContext context) {
        LootContextUser.super.validate(context);
        order().validate(context.forChild(".order"));
        for (int i = 0; i < modifiers().size(); i++) {
            final RestaurantOrderGenerator.ModifierEntry entry = modifiers().get(i);
            entry.chance().validate(context.forChild(".modifier_generator[" + i + "].chance"));
            entry.ingredient().validate(context.forChild(".modifier_generator[" + i + "].modififer"));
            // No need to validate success and failure multipliers as they are not evaluated in the same LootContext
        };
        for (int i = 0; i < modifiers().size(); i++) {
            final RestaurantOrderGenerator.RewardsEntry entry = rewards().get(i);
            entry.chance().validate(context.forChild(".reward_generator[" + i + "].chance"));
            DataValidationHelper.validateHolder(entry.rewards(), context, "reward_generator[" + i + "].rewards");
        };
    };

};
