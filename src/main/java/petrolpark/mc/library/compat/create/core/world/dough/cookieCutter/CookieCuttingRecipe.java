package petrolpark.mc.library.compat.create.core.world.dough.cookieCutter;

import java.util.List;

import javax.annotation.ParametersAreNonnullByDefault;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import petrolpark.mc.library.Petrolpark;
import petrolpark.mc.library.compat.create.core.world.dough.DoughCut;
import petrolpark.mc.library.compat.create.core.world.dough.DoughData;
import petrolpark.mc.library.compat.create.core.world.dough.ingredient.DoughIngredient;
import petrolpark.mc.library.compat.create.registry.PetrolparkCreateRecipeTypes;
import petrolpark.mc.library.core.data.recipe.ingredient.advanced.IAdvancedIngredient;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

@ParametersAreNonnullByDefault
public record CookieCuttingRecipe(List<IAdvancedIngredient<DoughData>> doughIngredients, Ingredient cutterIngredient, Holder<DoughCut> cut, List<ItemStack> results) implements Recipe<CookieCuttingRecipe.Input> {

    public static final RecipeType<Recipe<CookieCuttingRecipe.Input>> TYPE = RecipeType.simple(Petrolpark.asResource("cookie_cutting"));

    public static final MapCodec<CookieCuttingRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        DoughIngredient.STRICT_CODEC.listOf().fieldOf("dough").forGetter(CookieCuttingRecipe::doughIngredients),
        Ingredient.CODEC.fieldOf("cutter").forGetter(CookieCuttingRecipe::cutterIngredient),
        DoughCut.CODEC.fieldOf("cut").forGetter(CookieCuttingRecipe::cut),
        ItemStack.CODEC.listOf(1, 4).fieldOf("results").forGetter(CookieCuttingRecipe::results)
    ).apply(instance, CookieCuttingRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, CookieCuttingRecipe> STREAM_CODEC = StreamCodec.composite(
        DoughIngredient.STRICT_STREAM_CODEC.apply(ByteBufCodecs.list()), CookieCuttingRecipe::doughIngredients,
        Ingredient.CONTENTS_STREAM_CODEC, CookieCuttingRecipe::cutterIngredient,
        DoughCut.STREAM_CODEC, CookieCuttingRecipe::cut,
        ItemStack.STREAM_CODEC.apply(ByteBufCodecs.list()), CookieCuttingRecipe::results,
        CookieCuttingRecipe::new
    );
    
    @Override
    public boolean matches(CookieCuttingRecipe.Input input, Level level) {
        if (!cutterIngredient().test(input.cutter())) return false;
        for (IAdvancedIngredient<DoughData> doughIngredient : doughIngredients()) {
            if (!doughIngredient.test(input.dough())) return false;
        };
        return true;
    };

    @Override
    public ItemStack assemble(CookieCuttingRecipe.Input input, HolderLookup.Provider registries) {
        return getResultItem(registries);
    };

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 1;
    };

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries) {
        return results().get(0);
    };

    @Override
    public RecipeSerializer<CookieCuttingRecipe> getSerializer() {
        return PetrolparkCreateRecipeTypes.COOKIE_CUTTING.getSerializer();
    };

    @Override
    public RecipeType<Recipe<CookieCuttingRecipe.Input>> getType() {
        return TYPE;
    };

    public record Input(DoughData dough, ItemStack cutter) implements RecipeInput {

        @Override
        public ItemStack getItem(int index) {
            return cutter();
        };

        @Override
        public int size() {
            return 1;
        };

    };

    public static record Serializer(MapCodec<CookieCuttingRecipe> codec, StreamCodec<RegistryFriendlyByteBuf, CookieCuttingRecipe> streamCodec) implements RecipeSerializer<CookieCuttingRecipe> {

        public Serializer() {
            this(CODEC, STREAM_CODEC);
        };
    };
};
