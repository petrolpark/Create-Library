package petrolpark.mc.library.core.world.restaurant.order;

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
import petrolpark.mc.library.core.data.reward.info.IRewardInfo;
import petrolpark.mc.library.util.DataValidationHelper;

@ParametersAreNonnullByDefault
public record ServerRestaurantOrder(
    int id,
    IAdvancedIngredient<ItemStack> ingredient,
    List<RestaurantOrderModifier> modifiers,
    List<RewardEntry> rewardEntries
) implements IRestaurantOrder, LootContextUser {

    public static final Codec<ServerRestaurantOrder> CODEC = RecordCodecBuilder.create(instance ->
        instance.group(
            Codec.INT.fieldOf("id").forGetter(ServerRestaurantOrder::id),
            ItemAdvancedIngredient.CODEC.fieldOf("order").forGetter(ServerRestaurantOrder::ingredient),
            Codec.list(RestaurantOrderModifier.CODEC).fieldOf("modifiers").forGetter(ServerRestaurantOrder::modifiers),
            RewardEntry.LIST_CODEC.fieldOf("rewards").forGetter(ServerRestaurantOrder::rewardEntries)
        ).apply(instance, ServerRestaurantOrder::new)
    );

    public boolean test(ItemStack stack) {
        return ingredient().test(stack);
    };

    public ServerRestaurantOrder stripForMenu() {
        return new ServerRestaurantOrder(
            id(),
            ingredient(),
            modifiers().stream().filter(RestaurantOrderModifier::persistsToMenu).toList(),
            rewardEntries().stream().filter(RewardEntry::persistsToMenu).toList()
        );
    };
    
    @Override
    public List<RestaurantOrderModifier.Info> modifiersInfo() {
        return modifiers().stream()
            .mapMulti(RestaurantOrderModifier::supplyInfo)
            .toList();
    };

    @Override
    public List<IRewardInfo> rewardsInfo() {
        return rewardEntries().stream()
            .filter(RewardEntry::everVisible)
            .map(RewardEntry::reward)
            .map(Holder::value)
            .map(IReward::info)
            .toList();
    };

    public record RewardEntry(Holder<IReward> reward, IRestaurantOrder.Entry.Visibility visibility, boolean persistsToMenu) implements IRestaurantOrder.Entry {

        public static final Codec<List<RewardEntry>> LIST_CODEC = RecordCodecBuilder.<RewardEntry>create(instance ->
            instance
                .group(IReward.CODEC.fieldOf("reward").forGetter(RewardEntry::reward))
                .and(IRestaurantOrder.Entry.commonFields(instance))
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
