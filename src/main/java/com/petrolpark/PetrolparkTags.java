package com.petrolpark;

import com.petrolpark.core.contamination.Contaminant;
import com.petrolpark.util.Lang;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;

public class PetrolparkTags {

    /**
     * Copied from {@link com.simibubi.create.AllTags#optionalTag(Registry, ResourceLocation) Create source code}.
     * @return A TagKey
     */
    public static final <T> TagKey<T> optionalTag(Registry<T> registry, ResourceLocation id) {
		return TagKey.create(registry.key(), id);
	};

	public static final <T> TagKey<T> commonTag(Registry<T> registry, String path) {
		return optionalTag(registry, ResourceLocation.fromNamespaceAndPath("c", path));
	};

	public static final TagKey<Block> commonBlockTag(String path) {
		return commonTag(BuiltInRegistries.BLOCK, path);
	};

	public static final TagKey<Item> commonItemTag(String path) {
		return commonTag(BuiltInRegistries.ITEM, path);
	};

	public static final TagKey<Fluid> commonFluidTag(String path) {
		return commonTag(BuiltInRegistries.FLUID, path);
	};

    public enum Blocks {

        ;

        public static final TagKey<Block> common(String path) {
            return TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("c", path));
        };
    };

    public enum Contaminants {

        HIDDEN,
        ;

        public final TagKey<Contaminant> tag;

        Contaminants() {
            tag = TagKey.create(PetrolparkRegistries.Keys.CONTAMINANT, Petrolpark.asResource(Lang.asId(name())));
        };

        Contaminants(String path) {
            tag = TagKey.create(PetrolparkRegistries.Keys.CONTAMINANT, Petrolpark.asResource(path));
        };

        public boolean matches(Holder<Contaminant> contaminant) {
            return contaminant.is(tag);
        };
    };

    public enum BlockEntityTypes {

        CONTAMINABLE_KINETIC,
        ;

        public final TagKey<BlockEntityType<?>> tag;

        BlockEntityTypes() {
            tag = TagKey.create(Registries.BLOCK_ENTITY_TYPE, Petrolpark.asResource(Lang.asId(name())));
        };

        public boolean matches(BlockEntity blockEntity) {
            return matches(blockEntity.getType());
        };

        public boolean matches(BlockEntityType<?> blockEntityType) {
            return PetrolparkRegistries.getHolder(BuiltInRegistries.BLOCK_ENTITY_TYPE, blockEntityType).orElseThrow().is(tag);
        };
    };

    public enum EntityTypes {

        DOESNT_BLEED,
        ;

        public final TagKey<EntityType<?>> tag;

        EntityTypes() {
            tag = TagKey.create(Registries.ENTITY_TYPE, Petrolpark.asResource(Lang.asId(name())));
        };

        public boolean matches(EntityType<?> entityType) {
            return entityType.is(tag);
        };

        public boolean matches(Entity entity) {
            return matches(entity.getType());
        };
    };

    public enum Fluids {

        INCONTAMINABLE,
        ;

        public final TagKey<Fluid> tag;

        Fluids() {
            tag = TagKey.create(Registries.FLUID, Petrolpark.asResource(Lang.asId(name())));
        };

        @SuppressWarnings("deprecation")
        public boolean matches(Fluid fluid) {
            return fluid.is(tag);
        };

        @SuppressWarnings("deprecation")
        public boolean matches(FluidStack stack) {
            return stack.getFluid().is(tag);
        };

        public static final TagKey<Fluid> common(String path) {
            return TagKey.create(Registries.FLUID, ResourceLocation.fromNamespaceAndPath("c", path));
        };
    };

    public enum Items {

        INCONTAMINABLE,
        CONTAMINABLE_BLOCKS,
        ;

        public final TagKey<Item> tag;

        Items() {
            tag = TagKey.create(Registries.ITEM, Petrolpark.asResource(Lang.asId(name())));
        };

        @SuppressWarnings("deprecation")
        public boolean matches(Item item) {
            return item.builtInRegistryHolder().is(tag);
        };

        public boolean matches(ItemStack stack) {
            return stack.is(tag);
        };

        public static final TagKey<Item> common(String path) {
            return TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", path));
        };
    };

    public enum MenuTypes {

        ALWAYS_SHOWS_EXTENDED_INVENTORY,
        NEVER_SHOWS_EXTENDED_INVENTORY,
        ALLOWS_MANUAL_ONLY_CRAFTING,
        ;

        public final TagKey<MenuType<?>> tag;

        MenuTypes() {
            tag = TagKey.create(Registries.MENU, Petrolpark.asResource(Lang.asId(name())));
        };

        public boolean matches(AbstractContainerMenu menu) {
            try {
                return matches(menu.getType());
            } catch (UnsupportedOperationException e) {
                return false;
            }
        };

        public boolean matches(Holder.Reference<MenuType<?>> holder) {
            return holder.is(tag);
        };

        public boolean matches(MenuType<?> menuType) {
            return PetrolparkRegistries.getHolder(BuiltInRegistries.MENU, menuType).map(this::matches).orElse(false);
        };
    };

    public enum MobEffects {

        /**
         * Cancels panicked limb swing and sound
         */
        CANCELS_HURT_EFFECTS,
        CAUSES_INFERTILITY,
        PREVENTS_AGGRAVATING,
        PREVENTS_AGGRAVATING_OTHERS,
        ;

        public final TagKey<MobEffect> tag;

        private MobEffects() {
            tag = TagKey.create(Registries.MOB_EFFECT, Petrolpark.asResource(Lang.asId(name())));
        };

        public boolean matches(MobEffectInstance effectInstance) {
            return effectInstance.getEffect().is(tag);
        };

        public boolean matches(Holder<MobEffect> effect) {
            return effect.is(tag);
        };

    };

    public enum RecipeTypes {

        RECYCLABLE,
        ;

        public final TagKey<RecipeType<?>> tag;

        RecipeTypes() {
            tag = TagKey.create(Registries.RECIPE_TYPE, Petrolpark.asResource(Lang.asId(name())));
        };

        public boolean matches(Holder<RecipeType<?>> holder) {
            return holder.is(tag);
        };

        public boolean matches(RecipeType<?> recipeType) {
            return PetrolparkRegistries.getHolder(BuiltInRegistries.RECIPE_TYPE, recipeType).map(this::matches).orElse(false);
        };

        public boolean matches(Recipe<?> recipe) {
            return matches(recipe.getType());
        };
    };

};
