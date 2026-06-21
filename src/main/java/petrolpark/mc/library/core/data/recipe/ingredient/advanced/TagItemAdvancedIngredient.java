package petrolpark.mc.library.core.data.recipe.ingredient.advanced;

import java.util.stream.Stream;

import com.mojang.serialization.MapCodec;
import petrolpark.mc.library.registry.PetrolparkAdvancedIngredientTypes;
import petrolpark.mc.library.registry.PetrolparkRegistries;
import petrolpark.mc.library.util.Lang;
import petrolpark.mc.library.util.Lang.IndentedTooltipBuilder;
import petrolpark.mc.library.util.codec.CodecHelper;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public record TagItemAdvancedIngredient(TagKey<Item> tag) implements ItemAdvancedIngredient {

    public static final MapCodec<TagItemAdvancedIngredient> CODEC = CodecHelper.singleFieldMap(TagKey.codec(Registries.ITEM), "tag", TagItemAdvancedIngredient::tag, TagItemAdvancedIngredient::new);
    public static final StreamCodec<RegistryFriendlyByteBuf, TagItemAdvancedIngredient> STREAM_CODEC = StreamCodec.composite(
        ResourceLocation.STREAM_CODEC.map(rl -> TagKey.create(Registries.ITEM, rl), TagKey::location), TagItemAdvancedIngredient::tag,
        TagItemAdvancedIngredient::new
    );

    @Override
    public boolean test(ItemStack stack) {
        return stack.is(tag());
    };

    @Override
    public void addToDescription(IndentedTooltipBuilder description) {
        description.add(translateSimple(Lang.tag(tag())));
    };

    @Override
    public void addToCounterDescription(IndentedTooltipBuilder description) {
        description.add(translateInverse(Lang.tag(tag())));
    };

    @Override
    public INamedAdvancedIngredientType<ItemStack> getType() {
        return PetrolparkAdvancedIngredientTypes.ITEM_TAG.get();
    };

    public static record Type(String translationKey) implements INamedAdvancedIngredientType<ItemStack> {

        @Override
        public MapCodec<TagItemAdvancedIngredient> codec() {
            return CODEC;
        };

        @Override
        public StreamCodec<RegistryFriendlyByteBuf,TagItemAdvancedIngredient> streamCodec() {
            return STREAM_CODEC;
        };

        @Override
        public Stream<TagItemAdvancedIngredient> streamApplicableIngredients(Level level, ItemStack stack) {
            return PetrolparkRegistries.getHolder(level.registryAccess(), Registries.ITEM, stack.getItem())
                .stream()
                .flatMap(Holder::tags)
                .map(TagItemAdvancedIngredient::new);
        };

    };
    
};
