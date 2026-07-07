package petrolpark.mc.library.core.world.item.restaurant.order;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.ParametersAreNonnullByDefault;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.LootContextUser;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.NumberProviders;
import petrolpark.mc.library.core.data.recipe.ingredient.advanced.IAdvancedIngredient;
import petrolpark.mc.library.core.data.recipe.ingredient.advanced.ItemAdvancedIngredient;
import petrolpark.mc.library.core.data.recipe.ingredient.advanced.PassAdvancedIngredient;
import petrolpark.mc.library.util.Lang.IndentedTooltipBuilder;

@ParametersAreNonnullByDefault
public record RestaurantOrderModifier(
    IAdvancedIngredient<? super ItemStack> ingredient,
    NumberProvider successMultiplier, NumberProvider failureMultiplier,
    IRestaurantOrderThing.Visibility visibility, boolean persistsToMenu
) implements IRestaurantOrderThing, LootContextUser {

    public static final Codec<RestaurantOrderModifier> CODEC = Codec.lazyInitialized(() -> RecordCodecBuilder.create(instance -> 
        instance.group(
            ItemAdvancedIngredient.CODEC.optionalFieldOf("requirement", PassAdvancedIngredient.INSTANCE).forGetter(RestaurantOrderModifier::ingredient),
            NumberProviders.CODEC.fieldOf("success").forGetter(RestaurantOrderModifier::successMultiplier),
            NumberProviders.CODEC.optionalFieldOf("failure", ConstantValue.exactly(0f)).forGetter(RestaurantOrderModifier::failureMultiplier)
        ).and(IRestaurantOrderThing.commonFields(instance))
        .apply(instance, RestaurantOrderModifier::new)
    ));

    public List<Component> description(Level level) {
        List<Component> description = new ArrayList<>();
        ingredient.addToDescription(new IndentedTooltipBuilder(description));
        return description;
    };

    public NumberProvider multiplierForStack(ItemStack stack, Level level) {
        if (ingredient.test(stack)) return successMultiplier; else return failureMultiplier;
    };

    @Override
    public void validate(ValidationContext context) {
        LootContextUser.super.validate(context);
        successMultiplier().validate(context.forChild(".success_multiplier"));
        failureMultiplier().validate(context.forChild(".failure_multiplier"));
    };

};
