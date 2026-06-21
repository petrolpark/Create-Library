package petrolpark.mc.library.compat.create;

import petrolpark.mc.library.Petrolpark;
import petrolpark.mc.library.core.data.recipe.ingredient.advanced.CreateItemAttributeAdvancedIngredient;
import petrolpark.mc.library.util.Lang;
import com.simibubi.create.api.registry.CreateRegistries;
import com.simibubi.create.content.logistics.item.filter.attribute.ItemAttribute;
import com.simibubi.create.content.logistics.item.filter.attribute.ItemAttributeType;

import net.minecraft.core.Holder;
import net.minecraft.tags.TagKey;

public class PetrolparkCreateTags {
    
    public static enum ItemAttributes {

        /**
         * Doesn't use the {@code level} field of the {@link ItemAttribute#appliesTo(net.minecraft.world.item.ItemStack, net.minecraft.world.level.Level)} method.
         */
        LEVEL_INDEPDENDENT,
        /**
         * Can't be used as an {@link CreateItemAttributeAdvancedIngredient Ingredient Modifier}.
         */
        NOT_FOR_INGREDIENTS,
        ;

        public final TagKey<ItemAttributeType> tag;

        ItemAttributes() {
            tag = TagKey.create(CreateRegistries.ITEM_ATTRIBUTE_TYPE, Petrolpark.asResource(Lang.asId(name())));
        };

        public boolean matches(Holder<ItemAttributeType> type) {
            return type.is(tag);
        };
    };
};
