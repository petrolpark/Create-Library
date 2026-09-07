package petrolpark.mc.library.core.world.restaurant.order;

import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import javax.annotation.ParametersAreNonnullByDefault;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.Holder;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootContextUser;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.NumberProviders;
import petrolpark.mc.library.core.data.recipe.ingredient.randomizer.IngredientRandomizer;
import petrolpark.mc.library.core.data.reward.generator.IRewardGenerator;
import petrolpark.mc.library.registry.PetrolparkLootContextParamSets;
import petrolpark.mc.library.registry.PetrolparkRegistries;
import petrolpark.mc.library.util.DataValidationHelper;

@ParametersAreNonnullByDefault
public record RestaurantOrderGenerator(
    IngredientRandomizer order,
    List<RestaurantOrderGenerator.ModifierEntry> modifiers,
    List<RestaurantOrderGenerator.RewardsEntry> rewards
) implements LootContextUser {

    public static final Codec<RestaurantOrderGenerator> UNVALIDATED_DIRECT_CODEC = Codec.lazyInitialized(() -> RecordCodecBuilder.create(instance -> 
        instance.group(
            IngredientRandomizer.DIRECT_CODEC.fieldOf("order").forGetter(RestaurantOrderGenerator::order),
            Codec.list(RestaurantOrderGenerator.ModifierEntry.CODEC).optionalFieldOf("modifiers", Collections.emptyList()).forGetter(RestaurantOrderGenerator::modifiers),
            Codec.list(RestaurantOrderGenerator.RewardsEntry.CODEC).fieldOf("rewards").forGetter(RestaurantOrderGenerator::rewards)
        ).apply(instance, RestaurantOrderGenerator::new)
    ));

    public static final Codec<RestaurantOrderGenerator> DIRECT_CODEC = UNVALIDATED_DIRECT_CODEC.validate(DataValidationHelper.validateParamSet(PetrolparkLootContextParamSets.RESTAURANT_ORDER_GENERATION, "Restaurant Order Generator"));

    public static final Codec<Holder<RestaurantOrderGenerator>> CODEC = RegistryFileCodec.create(PetrolparkRegistries.Keys.RESTAURANT_ORDER_GENERATOR, DIRECT_CODEC);

    public ServerRestaurantOrder generate(LootContext context, int id, Stream<RestaurantOrderGenerator.ModifierEntry> additionalModifiers) {
        return new ServerRestaurantOrder(
            id,
            order().generate(context),
            Stream.concat(modifiers().stream(), additionalModifiers)
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
                        .map(reward -> new ServerRestaurantOrder.RewardEntry(reward, entry.visibility(), entry.persistsToMenu()))
                ).toList()
        );
    };

    public record ModifierEntry(NumberProvider chance, IngredientRandomizer ingredient, NumberProvider successMultiplier, NumberProvider failureMultiplier, IRestaurantOrder.Entry.Visibility visibility, boolean persistsToMenu) implements IRestaurantOrder.Entry {

        public static final Codec<RestaurantOrderGenerator.ModifierEntry> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                NumberProviders.CODEC.optionalFieldOf("chance", ConstantValue.exactly(1f)).forGetter(RestaurantOrderGenerator.ModifierEntry::chance),
                IngredientRandomizer.DIRECT_CODEC.fieldOf("modifier").forGetter(RestaurantOrderGenerator.ModifierEntry::ingredient),
                NumberProviders.CODEC.fieldOf("success").forGetter(RestaurantOrderGenerator.ModifierEntry::successMultiplier),
                NumberProviders.CODEC.optionalFieldOf("failure", ConstantValue.exactly(0f)).forGetter(RestaurantOrderGenerator.ModifierEntry::failureMultiplier)
            ).and(IRestaurantOrder.Entry.commonFields(instance))
            .apply(instance, RestaurantOrderGenerator.ModifierEntry::new)
        );
    };

    public record RewardsEntry(NumberProvider chance, Holder<IRewardGenerator> rewards, IRestaurantOrder.Entry.Visibility visibility, boolean persistsToMenu) implements IRestaurantOrder.Entry {

        public static final Codec<RestaurantOrderGenerator.RewardsEntry> CODEC = Codec.withAlternative(
            RecordCodecBuilder.create(instance -> 
                instance.group(
                    NumberProviders.CODEC.optionalFieldOf("chance", ConstantValue.exactly(1f)).forGetter(RestaurantOrderGenerator.RewardsEntry::chance),
                    IRewardGenerator.CODEC.fieldOf("rewards").forGetter(RestaurantOrderGenerator.RewardsEntry::rewards)
                ).and(IRestaurantOrder.Entry.commonFields(instance))
                .apply(instance, RestaurantOrderGenerator.RewardsEntry::new)
            ),
            IRewardGenerator.CODEC.xmap(RestaurantOrderGenerator.RewardsEntry::new, RestaurantOrderGenerator.RewardsEntry::rewards)
        );

        public RewardsEntry(Holder<IRewardGenerator> rewards) {
            this(ConstantValue.exactly(1f), rewards, IRestaurantOrder.Entry.Visibility.ALWAYS, true);
        };
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
