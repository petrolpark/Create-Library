package com.petrolpark.core.registrate;

import java.util.function.Function;
import java.util.function.Supplier;

import com.google.common.base.Suppliers;
import com.petrolpark.RequiresCreate;
import com.petrolpark.core.registrate.builder.PetrolparkBlockBuilder;
import com.simibubi.create.Create;
import com.simibubi.create.content.decoration.palettes.ConnectedGlassPaneBlock;
import com.simibubi.create.content.decoration.palettes.WindowBlock;
import com.simibubi.create.foundation.block.connected.AllCTTypes;
import com.simibubi.create.foundation.block.connected.CTSpriteShiftEntry;
import com.simibubi.create.foundation.block.connected.CTSpriteShifter;
import com.simibubi.create.foundation.block.connected.GlassPaneCTBehaviour;
import com.simibubi.create.foundation.block.connected.HorizontalCTBehaviour;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.tterrag.registrate.AbstractRegistrate;
import com.tterrag.registrate.providers.RegistrateBlockstateProvider;
import com.tterrag.registrate.providers.RegistrateRecipeProvider;
import com.tterrag.registrate.util.DataIngredient;
import com.tterrag.registrate.util.entry.BlockEntry;

import net.minecraft.core.BlockPos;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.client.model.generators.ModelFile.UncheckedModelFile;
import net.neoforged.neoforge.common.Tags;

@RequiresCreate
public class CreateWoodSet {
    
    public static final void addCreateEntries(AbstractRegistrate<?> registrate, WoodSetEntry woodSet) {

        final String woodName = ResourceLocation.parse(woodSet.woodType().name()).getPath();

        final ResourceLocation windowLoc = ResourceLocation.fromNamespaceAndPath(registrate.getModid(), woodName + "_window");
        final ResourceLocation textureLoc = windowLoc.withPrefix("block/");
        final Supplier<ResourceLocation> planksTextureLocSup = Suppliers.memoize(() -> woodSet.planks().getId().withPrefix("block/"));

        final Supplier<CTSpriteShiftEntry> spriteShiftEntrySup = Suppliers.memoize(() -> CTSpriteShifter.getCT(AllCTTypes.VERTICAL, textureLoc, textureLoc.withSuffix("_connected")));

        final BlockEntry<WindowBlock> window = registrate.block(windowLoc.getPath(), p -> new WindowBlock(p, false))
			.initialProperties(() -> Blocks.GLASS)
			.properties(p -> p
                .mapColor(woodSet.planksMapColor())
                .isValidSpawn(CreateWoodSet::never)
                .isRedstoneConductor(CreateWoodSet::never)
                .isSuffocating(CreateWoodSet::never)
                .isViewBlocking(CreateWoodSet::never)
            ).defaultLang()
            .loot((lt, b) -> lt.dropWhenSilkTouch(b))
			.blockstate((ctx, prov) -> prov.simpleBlock(ctx.get(), prov.models().cubeColumn(ctx.getName(), textureLoc, planksTextureLocSup.get())
                .renderType("cutout")
            )).onRegister(CreateRegistrate.connectedTextures(() -> new HorizontalCTBehaviour(spriteShiftEntrySup.get())))
            .tag(BlockTags.IMPERMEABLE)
			.transform(PetrolparkBlockBuilder::defaultBlockItem)
            .recipe((c, p) -> ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, c.get(), 2)
                .pattern(" # ")
                .pattern("#X#")
                .define('#', Ingredient.of(woodSet.planks()))
                .define('X', DataIngredient.tag(Tags.Items.GLASS_BLOCKS_COLORLESS).toVanilla())
                .unlockedBy("has_planks", RegistrateRecipeProvider.has(woodSet.planks()))
                .save(p)
            ).build()
            .register();

        final ResourceLocation paneLoc = windowLoc.withSuffix("_pane");

        final String paneModelPrefix = paneLoc.getPath() + "_";

        final Function<RegistrateBlockstateProvider, ModelFile>
        postModelProvider = getPaneModelProvider(paneModelPrefix, "post", textureLoc, planksTextureLocSup),
        sideModelProvider = getPaneModelProvider(paneModelPrefix, "side", textureLoc, planksTextureLocSup),
        sideAltModelProvider = getPaneModelProvider(paneModelPrefix, "side_alt", textureLoc, planksTextureLocSup),
        noSideModelProvider = getPaneModelProvider(paneModelPrefix, "noside", textureLoc, planksTextureLocSup),
        noSideAltModelProvider = getPaneModelProvider(paneModelPrefix, "noside_alt", textureLoc, planksTextureLocSup);

        registrate.block(paneLoc.getPath(), ConnectedGlassPaneBlock::new)
			.initialProperties(() -> Blocks.GLASS_PANE)
			.properties(p -> p
                .mapColor(woodSet.planksMapColor())
            ).defaultLang()
            .loot((lt, b) -> lt.dropWhenSilkTouch(b))
            .blockstate((ctx, prov) -> prov.paneBlock(ctx.get(),
                    postModelProvider.apply(prov),
                    sideModelProvider.apply(prov),
                    sideAltModelProvider.apply(prov),
                    noSideModelProvider.apply(prov),
                    noSideAltModelProvider.apply(prov)
                )
            ).onRegister(CreateRegistrate.connectedTextures(() -> new GlassPaneCTBehaviour(spriteShiftEntrySup.get())))
            .tag(Tags.Blocks.GLASS_PANES)
            .item()
            .model((ctx, prov) -> prov.generated(ctx, textureLoc))
            .tag(Tags.Items.GLASS_PANES)
            .recipe((ctx, prov) -> ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ctx.get(), 16)
                .pattern("###")
                .pattern("###")
                .define('#', window)
                .unlockedBy("has_window", RegistrateRecipeProvider.has(window))
                .save(prov)
			).build()
            .register();
    };

    public static final boolean never(BlockState p_235436_0_, BlockGetter p_235436_1_, BlockPos p_235436_2_) {
        return false;
	};

	public static final boolean never(BlockState p_235427_0_, BlockGetter p_235427_1_, BlockPos p_235427_2_, EntityType<?> p_235427_3_) {
		return false;
	};

    public static final Function<RegistrateBlockstateProvider, ModelFile> getPaneModelProvider(String prefix, String partial, ResourceLocation paneTexture, Supplier<ResourceLocation> edgeTextureSup) {
		return prov -> prov.models()
            .getBuilder(prefix + partial)
            .parent(new UncheckedModelFile(Create.asResource("block/connected_glass_pane/" + partial)))
			.texture("pane", paneTexture)
			.texture("edge", edgeTextureSup.get())
            .renderType("cutout");
	};
};
