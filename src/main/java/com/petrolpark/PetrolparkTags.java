package com.petrolpark;

import java.util.Collections;

import com.petrolpark.contamination.Contaminant;
import com.petrolpark.team.data.ITeamDataType;
import com.petrolpark.util.Lang;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;

public class PetrolparkTags {

    /**
     * Copied from {@link com.simibubi.create.AllTags#optionalTag(IForgeRegistry, ResourceLocation) Create source code}.
     * @return A TagKey
     */
    public static <T> TagKey<T> optionalTag(IForgeRegistry<T> registry, ResourceLocation id) {
		return registry.tags().createOptionalTagKey(id, Collections.emptySet());
	};

	public static <T> TagKey<T> forgeTag(IForgeRegistry<T> registry, String path) {
		return optionalTag(registry, new ResourceLocation("forge", path));
	};

	public static TagKey<Block> forgeBlockTag(String path) {
		return forgeTag(ForgeRegistries.BLOCKS, path);
	};

	public static TagKey<Item> forgeItemTag(String path) {
		return forgeTag(ForgeRegistries.ITEMS, path);
	};

	public static TagKey<Fluid> forgeFluidTag(String path) {
		return forgeTag(ForgeRegistries.FLUIDS, path);
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
            return ForgeRegistries.BLOCK_ENTITY_TYPES.getHolder(blockEntityType).orElseThrow().is(tag);
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

        public boolean matches(MenuType<?> menuType) {
            return ForgeRegistries.MENU_TYPES.getHolder(menuType).map(h -> h.is(tag)).orElse(false);
        };
    };

    public enum Contaminants {

        HIDDEN,
        NOT_PRESERVED_CRUSHING("not_preserved/crushing")
        ;

        public final TagKey<Contaminant> tag;

        Contaminants() {
            tag = TagKey.create(PetrolparkRegistries.Keys.CONTAMINANT, Petrolpark.asResource(Lang.asId(name())));
        };

        Contaminants(String path) {
            tag = TagKey.create(PetrolparkRegistries.Keys.CONTAMINANT, Petrolpark.asResource(path));
        };

        public boolean matches(Contaminant contaminant) {
            return PetrolparkRegistries.getDataRegistry(PetrolparkRegistries.Keys.CONTAMINANT).getHolder(PetrolparkRegistries.getDataRegistry(PetrolparkRegistries.Keys.CONTAMINANT).getId(contaminant)).orElseThrow().is(tag);
        };
    }

    public enum TeamDataTypes {

        LOST_ON_PLAYER_DEATH,
        ;

        public final TagKey<ITeamDataType<?>> tag;

        TeamDataTypes() {
            tag = TagKey.create(PetrolparkRegistries.Keys.TEAM_DATA_TYPE, Petrolpark.asResource(Lang.asId(name())));
        };

        public boolean matches(ITeamDataType<?> teamDataType) {
            return PetrolparkRegistries.getRegistry(PetrolparkRegistries.Keys.TEAM_DATA_TYPE).getHolder(teamDataType).orElseThrow().is(tag);
        };
    };
};
