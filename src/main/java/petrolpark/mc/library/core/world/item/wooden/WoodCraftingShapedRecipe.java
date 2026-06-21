package petrolpark.mc.library.core.world.item.wooden;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Stream;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import petrolpark.mc.library.core.world.item.recycling.IRecyclableRecipe;
import petrolpark.mc.library.core.world.item.recycling.RecyclingManager;
import petrolpark.mc.library.core.world.item.recycling.RecyclingOutputs;
import petrolpark.mc.library.registry.PetrolparkDataComponentTypes;
import petrolpark.mc.library.registry.PetrolparkRecipeSerializers;
import petrolpark.mc.library.util.WoodHelper;
import petrolpark.mc.library.util.WoodHelper.Wood;

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

public class WoodCraftingShapedRecipe extends ShapedRecipe implements IRecyclableRecipe {

    public static final Ingredient PLANKS_INGREDIENT = Ingredient.of(ItemTags.PLANKS);
    public static final Ingredient SLAB_INGREDIENT = Ingredient.of(ItemTags.WOODEN_SLABS);
    public static final Ingredient STAIRS_INGREDIENT = Ingredient.of(ItemTags.WOODEN_STAIRS);
    public static final Ingredient FENCE_INGREDIENT = Ingredient.of(Tags.Items.FENCES_WOODEN);
    public static final Ingredient FENCE_GATE_INGREDIENT = Ingredient.of(Tags.Items.FENCE_GATES_WOODEN);
    public static final Ingredient BUTTON_INGREDIENT = Ingredient.of(ItemTags.WOODEN_BUTTONS);
    public static final Ingredient LOG_INGREDIENT = Ingredient.of(ItemTags.LOGS);
    public static final Ingredient STRIPPED_LOG_INGREDIENT = Ingredient.of(Tags.Items.STRIPPED_LOGS);
    public static final Ingredient LEAVES_INGREDIENT = Ingredient.fromValues(Stream.of(new Ingredient.TagValue(ItemTags.LEAVES), new Ingredient.TagValue(ItemTags.WART_BLOCKS)));
    public static final Ingredient SAPLING_INGREDIENT = Ingredient.of(ItemTags.SAPLINGS);
    public static final Ingredient PRESSURE_PLATE_INGREDIENT = Ingredient.of(ItemTags.WOODEN_PRESSURE_PLATES);
    public static final Ingredient DOOR_INGREDIENT = Ingredient.of(ItemTags.WOODEN_DOORS);
    public static final Ingredient TRAPDOOR_INGREDIENT = Ingredient.of(ItemTags.WOODEN_TRAPDOORS);
    public static final Ingredient SIGN_INGREDIENT = Ingredient.of(ItemTags.SIGNS);
    public static final Ingredient HANGING_SIGN_INGREDIENT = Ingredient.of(ItemTags.HANGING_SIGNS);
    public static final Ingredient BOAT_INGREDIENT = Ingredient.of(ItemTags.BOATS);

    protected static final Map<Character, Ingredient> BUILT_IN_INGREDIENTS = new HashMap<>();
    protected static final Map<Ingredient, Function<? super ItemStack, Wood>> WOOD_GETTERS = new HashMap<>();

    public static final Set<Map.Entry<Ingredient, Function<? super ItemStack, Wood>>> woodGetterEntries() {
        return WOOD_GETTERS.entrySet();
    };

    public static final void register(Character character, Ingredient ingredient, Function<? super ItemStack, Wood> woodGetter) {
        if (!ingredient.isSimple()) throw new IllegalArgumentException("Built-in ingredients must be simple");
        BUILT_IN_INGREDIENTS.put(character, ingredient);
        WOOD_GETTERS.put(ingredient, woodGetter);
    };

    static {
        register('P', PLANKS_INGREDIENT, WoodHelper::getWoodFromPlanks);
        register('s', SLAB_INGREDIENT, WoodHelper::getWoodFromSlab);
        register('S', STAIRS_INGREDIENT, WoodHelper::getWoodFromStairs);
        register('F', FENCE_INGREDIENT, WoodHelper::getWoodFromFence);
        register('G', FENCE_GATE_INGREDIENT, WoodHelper::getWoodFromFenceGate);
        register('b', BUTTON_INGREDIENT, WoodHelper::getWoodFromButton);
        register('L', LOG_INGREDIENT, WoodHelper::getWoodFromLog);
        register('l', STRIPPED_LOG_INGREDIENT, WoodHelper::getWoodFromStrippedLog);
        register('E', LEAVES_INGREDIENT, WoodHelper::getWoodFromLeaves);
        register('A', SAPLING_INGREDIENT, WoodHelper::getWoodFromSapling);
        register('p', PRESSURE_PLATE_INGREDIENT, WoodHelper::getWoodFromPressurePlate);
        register('D', DOOR_INGREDIENT, WoodHelper::getWoodFromDoor);
        register('T', TRAPDOOR_INGREDIENT, WoodHelper::getWoodFromTrapdoor);
        register('I', SIGN_INGREDIENT, WoodHelper::getWoodFromSign);
        register('H', HANGING_SIGN_INGREDIENT, WoodHelper::getWoodFromHangingSign);
        register('B', BOAT_INGREDIENT, WoodHelper::getWoodFromBoatItem);
    };

    @Nullable
    public static final Wood getWood(ItemStack stack) {
        if (stack.has(PetrolparkDataComponentTypes.WOOD)) return stack.get(PetrolparkDataComponentTypes.WOOD);
        for (final Map.Entry<Ingredient, Function<? super ItemStack, Wood>> entry : WOOD_GETTERS.entrySet()) {
            if (entry.getKey().test(stack)) return entry.getValue().apply(stack);
        };
        return null;
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
        boolean hasWoodIngredient = false;
        for (final Map.Entry<Character, Ingredient> builtInKey : BUILT_IN_INGREDIENTS.entrySet()) {
            if (existingKeys.containsKey(builtInKey.getKey())) return DataResult.error(() -> "Invalid pattern: '"+builtInKey.getKey()+"'' is a reserved symbol");
            for (String line : patternData.pattern()) if (line.indexOf(builtInKey.getKey()) != -1) {
                allKeys.put(builtInKey);
                hasWoodIngredient = true;
                break;
            };
        };
        if (!hasWoodIngredient) for (final Ingredient ingredient : existingKeys.values()) {
            if (ingredient.isSimple() && Stream.of(ingredient.getItems()).allMatch(s -> s.has(PetrolparkDataComponentTypes.WOOD))) hasWoodIngredient = true;
        };
        if (!hasWoodIngredient) return DataResult.error(() -> "Invalid pattern: must use at least one built-in wood item symbol");
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

    public ItemStack getResult(Wood wood) {
        final ItemStack result = this.result.copy();
        result.set(PetrolparkDataComponentTypes.WOOD, wood);
        return result;
    };

    public ItemStack assemble(@Nonnull CraftingInput input, boolean mirrored) {
        Wood wood = null;
        for (int y = 0; y < getPattern().height(); y++) {
            for (int x = 0; x < getPattern().width(); x++) {
                final Ingredient ingredient = mirrored ? getPattern().ingredients().get(getPattern().width() - x - 1 + y * getPattern().width()) : getPattern().ingredients().get(x + y * getPattern().width());
                final ItemStack stack = input.getItem(x, y);
                
                if (!ingredient.test(stack)) return ItemStack.EMPTY;

                final Function<? super ItemStack, Wood> woodGetter = WOOD_GETTERS.get(ingredient);
                if (woodGetter != null || stack.has(PetrolparkDataComponentTypes.WOOD)) {
                    final Wood thisWood = woodGetter != null ? woodGetter.apply(stack) : stack.get(PetrolparkDataComponentTypes.WOOD);
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
            return getResult(wood);
        }
    };

    public Stream<Ingredient> streamSpecificIngredientsFor(ItemStack result) {
        final Wood wood = result.get(PetrolparkDataComponentTypes.WOOD);
        if (wood == null) return Stream.empty();
        return streamSpecificIngredientsFor(wood);
    };

    public Stream<Ingredient> streamSpecificIngredientsFor(Wood wood) {
        return getIngredients().stream()
            .map(ingredient -> {
                final Function<? super ItemStack, Wood> woodGetter = WOOD_GETTERS.get(ingredient);
                if (woodGetter != null) {
                    return Ingredient.of(Stream.of(ingredient.getItems()).filter(stack -> woodGetter.apply(stack).equals(wood)));
                } else if (ingredient.isSimple()) {

                    boolean isLikelyToBeWoodIngredient = false;
                    List<ItemStack> ingredients = new ArrayList<>();
                    for (ItemStack stack : ingredient.getItems()) {
                        Wood stackWood = getWood(stack);
                        if (stackWood == null) continue;
                        isLikelyToBeWoodIngredient = true;
                        if (wood.equals(stackWood)) {
                            ingredients.add(stack);
                        };
                    };

                    if (isLikelyToBeWoodIngredient) return Ingredient.of(ingredients.stream());

                    return Ingredient.of(Stream.of(ingredient.getItems()).map(stack -> {
                        if (stack.has(PetrolparkDataComponentTypes.WOOD)) {
                            final ItemStack copy = stack.copy();
                            copy.set(PetrolparkDataComponentTypes.WOOD, wood);
                            return copy;
                        } else return stack;
                    }));
                } else {
                    return ingredient;
                }
            });
    };

    public Stream<ItemStack> streamSpecificStacksFor(Wood wood) {
        return streamSpecificIngredientsFor(wood).map(ingredient -> ingredient.getItems()[0]);
    };

    @Override
    public Optional<RecyclingOutputs> getRecyclingOutputs(Level level, ItemStack stack) {
        return Optional.of(streamSpecificIngredientsFor(stack).map(RecyclingManager::getInverse).collect(RecyclingOutputs.COLLECTOR)).filter(RecyclingOutputs::hasOutputs);
    };

    @Override
    public WoodCraftingShapedRecipe.Serializer getSerializer() {
        return PetrolparkRecipeSerializers.WOOD_CRAFTING_SHAPED.get();
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
