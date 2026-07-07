package petrolpark.mc.library.core.world.item.restaurant.order;

import java.util.List;

import javax.annotation.ParametersAreNonnullByDefault;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.Holder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContextUser;
import net.minecraft.world.level.storage.loot.ValidationContext;
import petrolpark.mc.library.core.data.recipe.ingredient.advanced.IAdvancedIngredient;
import petrolpark.mc.library.core.data.recipe.ingredient.advanced.ItemAdvancedIngredient;
import petrolpark.mc.library.core.data.reward.IReward;
import petrolpark.mc.library.util.DataValidationHelper;

@ParametersAreNonnullByDefault
public record RestaurantOrder(
    int id,
    IAdvancedIngredient<? super ItemStack> ingredient,
    List<RestaurantOrderModifier> modifiers,
    List<RewardEntry> rewardEntries
) implements LootContextUser {

    public static final Codec<RestaurantOrder> CODEC = RecordCodecBuilder.create(instance ->
        instance.group(
            Codec.INT.fieldOf("id").forGetter(RestaurantOrder::id),
            ItemAdvancedIngredient.CODEC.fieldOf("order").forGetter(RestaurantOrder::ingredient),
            Codec.list(RestaurantOrderModifier.CODEC).fieldOf("modifiers").forGetter(RestaurantOrder::modifiers),
            RewardEntry.LIST_CODEC.fieldOf("rewards").forGetter(RestaurantOrder::rewardEntries)
        ).apply(instance, RestaurantOrder::new)
    );

    public boolean test(ItemStack stack) {
        return ingredient().test(stack);
    };

    public RestaurantOrder stripForMenu() {
        return new RestaurantOrder(
            id(),
            ingredient(),
            modifiers().stream().filter(RestaurantOrderModifier::persistsToMenu).toList(),
            rewardEntries().stream().filter(RewardEntry::persistsToMenu).toList()
        );
    };

    public record RewardEntry(Holder<IReward> reward, IRestaurantOrderThing.Visibility visibility, boolean persistsToMenu) implements IRestaurantOrderThing {

        public static final Codec<List<RewardEntry>> LIST_CODEC = RecordCodecBuilder.<RewardEntry>create(instance ->
            instance
                .group(IReward.CODEC.fieldOf("reward").forGetter(RewardEntry::reward))
                .and(IRestaurantOrderThing.commonFields(instance))
                .apply(instance, RewardEntry::new)
            ).listOf();
    };

    @Override
    public void validate(ValidationContext context) {
        LootContextUser.super.validate(context);
        for (int i = 0; i < modifiers().size(); i++) modifiers().get(i).validate(context.forChild(".modifier[" + i + "]"));
        for (int i = 0; i < rewardEntries().size(); i++) DataValidationHelper.validateHolder(rewardEntries().get(i).reward(), context, "reward[" + i + "]");
    };
};
