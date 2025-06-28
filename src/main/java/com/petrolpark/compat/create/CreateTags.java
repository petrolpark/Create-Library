package com.petrolpark.compat.create;

import com.petrolpark.Petrolpark;
import com.petrolpark.core.recipe.ingredient.modifier.CreateItemAttributeIngredientModifier;
import com.petrolpark.util.Lang;
import com.simibubi.create.api.registry.CreateRegistries;
import com.simibubi.create.content.logistics.item.filter.attribute.ItemAttribute;
import com.simibubi.create.content.logistics.item.filter.attribute.ItemAttributeType;

import net.minecraft.core.Holder;
import net.minecraft.tags.TagKey;

public class CreateTags {
    
    public static enum ItemAttributes {

        /**
         * Doesn't use the {@code level} field of the {@link ItemAttribute#appliesTo(net.minecraft.world.item.ItemStack, net.minecraft.world.level.Level)} method.
         */
        LEVEL_INDEPDENDENT,
        /**
         * Can't be used as an {@link CreateItemAttributeIngredientModifier Ingredient Modifier}.
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
