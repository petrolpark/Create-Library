package petrolpark.mc.library.core.world.restaurant.order;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import javax.annotation.ParametersAreNonnullByDefault;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.LootContextUser;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.NumberProviders;
import petrolpark.mc.library.core.data.numberProvider.NumberEstimate;
import petrolpark.mc.library.core.data.recipe.ingredient.advanced.IAdvancedIngredient;
import petrolpark.mc.library.core.data.recipe.ingredient.advanced.ItemAdvancedIngredient;
import petrolpark.mc.library.core.data.recipe.ingredient.advanced.PassAdvancedIngredient;
import petrolpark.mc.library.util.Lang.IndentedTooltipBuilder;

@ParametersAreNonnullByDefault
public record RestaurantOrderModifier(
    IAdvancedIngredient<? super ItemStack> ingredient,
    NumberProvider successMultiplier, NumberProvider failureMultiplier,
    IRestaurantOrder.Entry.Visibility visibility, boolean persistsToMenu
    //TODO comment
) implements IRestaurantOrder.Entry, LootContextUser {

    public static final Codec<RestaurantOrderModifier> CODEC = Codec.lazyInitialized(() -> RecordCodecBuilder.create(instance -> 
        instance.group(
            ItemAdvancedIngredient.CODEC.optionalFieldOf("requirement", PassAdvancedIngredient.INSTANCE).forGetter(RestaurantOrderModifier::ingredient),
            NumberProviders.CODEC.fieldOf("success").forGetter(RestaurantOrderModifier::successMultiplier),
            NumberProviders.CODEC.optionalFieldOf("failure", ConstantValue.exactly(0f)).forGetter(RestaurantOrderModifier::failureMultiplier)
        ).and(IRestaurantOrder.Entry.commonFields(instance))
        .apply(instance, RestaurantOrderModifier::new)
    ));

    public List<Component> description(Level level) {
        final List<Component> description = new ArrayList<>();
        ingredient.addToDescription(new IndentedTooltipBuilder.Impl(description));
        return description;
    };

    public NumberProvider multiplierForStack(ItemStack stack, Level level) {
        if (ingredient.test(stack)) return successMultiplier; else return failureMultiplier;
    };

    public void supplyInfo(Consumer<RestaurantOrderModifier.Info> consumer) {
        if (everVisible()) consumer.accept(new RestaurantOrderModifier.Info(ingredient(), NumberEstimate.get(successMultiplier()), NumberEstimate.get(failureMultiplier())));
    };

    @Override
    public void validate(ValidationContext context) {
        LootContextUser.super.validate(context);
        successMultiplier().validate(context.forChild(".successMultiplier"));
        failureMultiplier().validate(context.forChild(".failureMultiplier"));
    };

    public record Info(IAdvancedIngredient<? super ItemStack> ingredient, NumberEstimate successMultiplier, NumberEstimate failureMultiplier) {
      
        public static final StreamCodec<RegistryFriendlyByteBuf, RestaurantOrderModifier.Info> STREAM_CODEC = StreamCodec.composite(
            ItemAdvancedIngredient.STREAM_CODEC, RestaurantOrderModifier.Info::ingredient,
            NumberEstimate.STREAM_CODEC, RestaurantOrderModifier.Info::successMultiplier,
            NumberEstimate.STREAM_CODEC, RestaurantOrderModifier.Info::failureMultiplier,
            RestaurantOrderModifier.Info::new
        );
    };

};
