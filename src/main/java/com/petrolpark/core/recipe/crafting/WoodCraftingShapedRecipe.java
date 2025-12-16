package com.petrolpark.core.recipe.crafting;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import javax.annotation.Nonnull;

import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.petrolpark.PetrolparkDataComponents;
import com.petrolpark.util.WoodHelper;
import com.petrolpark.util.WoodHelper.Wood;

import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.item.crafting.ShapedRecipePattern;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.Tags;

public class WoodCraftingShapedRecipe extends ShapedRecipe {

    public static final Ingredient PLANKS_INGREDIENT = Ingredient.of(ItemTags.PLANKS);
    public static final Ingredient LOGS_INGREDIENT = Ingredient.of(ItemTags.LOGS);
    public static final Ingredient STRIPPED_LOGS_INGREDIENT = Ingredient.of(Tags.Items.STRIPPED_LOGS);

    protected static final Map<Character, Ingredient> BUILT_IN_INGREDIENTS = new HashMap<>();
    protected static final Map<Ingredient, Function<? super ItemStack, Wood>> WOOD_GETTERS = new HashMap<>();

    public static final void register(Character character, Ingredient ingredient, Function<? super ItemStack, Wood> woodGetter) {
        BUILT_IN_INGREDIENTS.put(character, ingredient);
        WOOD_GETTERS.put(ingredient, woodGetter);
    };

    static {
        register('P', PLANKS_INGREDIENT, WoodHelper::getWoodFromPlanks);
        register('L', LOGS_INGREDIENT, WoodHelper::getWoodFromLog);
        register('S', STRIPPED_LOGS_INGREDIENT, WoodHelper::getWoodFromStrippedLog);
    };

    public static final MapCodec<ShapedRecipePattern> PATTERN_MAP_CODEC = ShapedRecipePattern.Data.MAP_CODEC.flatXmap(WoodCraftingShapedRecipe::unpackPatternData, WoodCraftingShapedRecipe::packPattern);

    @SuppressWarnings("null")
    public static final MapCodec<WoodCraftingShapedRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        Codec.STRING.optionalFieldOf("group", "").forGetter(WoodCraftingShapedRecipe::getGroup),
        CraftingBookCategory.CODEC.fieldOf("category").orElse(CraftingBookCategory.MISC).forGetter(WoodCraftingShapedRecipe::category),
        PATTERN_MAP_CODEC.forGetter(WoodCraftingShapedRecipe::getPattern),
        ItemStack.STRICT_CODEC.fieldOf("result").forGetter(recipe -> recipe.getResultItem(null)),
        Codec.BOOL.optionalFieldOf("show_notification", Boolean.valueOf(true)).forGetter(WoodCraftingShapedRecipe::showNotification)
    ).apply(instance, WoodCraftingShapedRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, WoodCraftingShapedRecipe> STREAM_CODEC = StreamCodec.of(
        WoodCraftingShapedRecipe.Serializer::toNetwork, WoodCraftingShapedRecipe.Serializer::fromNetwork
    );

    public WoodCraftingShapedRecipe(String group, CraftingBookCategory category, ShapedRecipePattern pattern, ItemStack result, boolean showNotification) {
        super(group, category, pattern, result, showNotification);
    };

    public ShapedRecipePattern getPattern() {
        return pattern;
    };

    public static final DataResult<ShapedRecipePattern> unpackPatternData(ShapedRecipePattern.Data patternData) {
        final Map<Character, Ingredient> existingKeys = patternData.key();
        final ImmutableMap.Builder<Character, Ingredient> allKeys = ImmutableMap.builder();
        allKeys.putAll(existingKeys);
        boolean usesBuiltInKeys = false;
        for (final Map.Entry<Character, Ingredient> builtInKey : BUILT_IN_INGREDIENTS.entrySet()) {
            if (existingKeys.containsKey(builtInKey.getKey())) return DataResult.error(() -> "Invalid pattern: '"+builtInKey.getKey()+"'' is a reserved symbol");
            for (String line : patternData.pattern()) if (line.indexOf(builtInKey.getKey()) != -1) {
                allKeys.put(builtInKey);
                usesBuiltInKeys = true;
                break;
            };
        };
        if (!usesBuiltInKeys) return DataResult.error(() -> "Invalid pattern: must use at least one built-in wood item symbol");
        return ShapedRecipePattern.unpack(new ShapedRecipePattern.Data(allKeys.build(), patternData.pattern()));
    };

    public static final DataResult<ShapedRecipePattern.Data> packPattern(ShapedRecipePattern pattern) {
        if (pattern.data.isEmpty()) return DataResult.error(() -> "Cannot encode unpacked recipe");
        final ImmutableMap.Builder<Character, Ingredient> strippedKeys = ImmutableMap.builder();
        pattern.data.get().key().entrySet().stream().filter(entry -> !BUILT_IN_INGREDIENTS.containsKey(entry.getKey())).forEach(strippedKeys::put);
        return DataResult.success(new ShapedRecipePattern.Data(strippedKeys.build(), pattern.data.get().pattern()));
    };

    @Override
    public boolean matches(@Nonnull CraftingInput input, @Nonnull Level level) {
        if (input.ingredientCount() != getPattern().ingredientCount) {
            return false;
        } else {
            if (input.width() == getPattern().width() && input.height() == getPattern().height()) {
                if (!getPattern().symmetrical && !assemble(input, true).isEmpty()) return true;
                if (!assemble(input, false).isEmpty()) return true;
            };
            return false;
        }
    };
    
    @Override
    public ItemStack assemble(@Nonnull CraftingInput input, @Nonnull HolderLookup.Provider registries) {
        if (!getPattern().symmetrical) {
            final ItemStack stack = assemble(input, true);
            if (!stack.isEmpty()) return stack;
        };
        return assemble(input, false);
    };

    public ItemStack assemble(@Nonnull CraftingInput input, boolean mirrored) {
        Wood wood = null;
        for (int y = 0; y < getPattern().height(); y++) {
            for (int x = 0; x < getPattern().width(); x++) {
                final Ingredient ingredient = mirrored ? getPattern().ingredients().get(getPattern().width() - x - 1 + y * getPattern().width()) : getPattern().ingredients().get(x + y * getPattern().width());
                final ItemStack stack = input.getItem(x, y);
                
                if (!ingredient.test(stack)) return ItemStack.EMPTY;

                final Function<? super ItemStack, Wood> woodGetter = WOOD_GETTERS.get(ingredient);
                if (woodGetter != null) {
                    final Wood thisWood = woodGetter.apply(stack);
                    if (thisWood == null) return ItemStack.EMPTY;
                    if (wood != null) {
                        if (!wood.equals(thisWood)) return ItemStack.EMPTY;
                    } else { 
                        wood = thisWood;
                    }
                };
            };
        };

        if (wood == null) {
            return ItemStack.EMPTY;
        } else {
            final ItemStack result = this.result.copy();
            result.set(PetrolparkDataComponents.WOOD, wood);
            return result;
        }
    };

    public static class Serializer implements RecipeSerializer<WoodCraftingShapedRecipe> {

        @Override
        public MapCodec<WoodCraftingShapedRecipe> codec() {
            return CODEC;
        };

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, WoodCraftingShapedRecipe> streamCodec() {
            return STREAM_CODEC;
        };

        protected static final WoodCraftingShapedRecipe fromNetwork(RegistryFriendlyByteBuf buffer) {
            final String group = buffer.readUtf();
            final CraftingBookCategory category = buffer.readEnum(CraftingBookCategory.class);
            final ShapedRecipePattern pattern = ShapedRecipePattern.STREAM_CODEC.decode(buffer);
            final ItemStack itemstack = ItemStack.STREAM_CODEC.decode(buffer);
            final boolean showNotification = buffer.readBoolean();
            return new WoodCraftingShapedRecipe(group, category, pattern, itemstack, showNotification);
        };

        protected static final void toNetwork(RegistryFriendlyByteBuf buffer, WoodCraftingShapedRecipe recipe) {
            buffer.writeUtf(recipe.getGroup());
            buffer.writeEnum(recipe.category());
            ShapedRecipePattern.STREAM_CODEC.encode(buffer, recipe.pattern);
            ItemStack.STREAM_CODEC.encode(buffer, recipe.result);
            buffer.writeBoolean(recipe.showNotification());
        };
    };
    
};
