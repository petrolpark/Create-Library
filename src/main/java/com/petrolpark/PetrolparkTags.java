package com.petrolpark;

import com.simibubi.create.foundation.utility.Lang;

import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.registries.ForgeRegistries;

public class PetrolparkTags {
    public enum PetrolparkMenuTypeTags {

        ALWAYS_SHOWS_EXTENDED_INVENTORY,
        NEVER_SHOWS_EXTENDED_INVENTORY,
        ALLOWS_MANUAL_ONLY_CRAFTING;

        public final TagKey<MenuType<?>> tag;

        PetrolparkMenuTypeTags() {
            tag = TagKey.create(Registries.MENU, Petrolpark.asResource(Lang.asId(name())));
        };

        public boolean matches(AbstractContainerMenu menu) {
            try {
                return matches(menu.getType());
            } catch (UnsupportedOperationException e) {
                return false;
            }
        };

        public boolean matches(MenuType<?> menuType) {
            return ForgeRegistries.MENU_TYPES.getHolder(menuType).orElseThrow().is(tag);
        };
    };
};
