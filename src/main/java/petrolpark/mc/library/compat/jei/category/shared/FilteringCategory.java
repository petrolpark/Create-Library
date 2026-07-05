package petrolpark.mc.library.compat.jei.category.shared;

import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.compat.jei.category.BasinCategory;
import com.simibubi.create.compat.jei.category.CreateRecipeCategory;
import com.simibubi.create.compat.jei.category.animations.AnimatedBlazeBurner;
import com.simibubi.create.compat.jei.category.animations.AnimatedKinetics;
import com.simibubi.create.compat.jei.category.animations.AnimatedSpout;
import com.simibubi.create.content.processing.basin.BasinRecipe;
import com.simibubi.create.content.processing.recipe.HeatCondition;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.recipe.IFocusGroup;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.createmod.catnip.gui.UIRenderHelper;
import net.createmod.catnip.platform.NeoForgeCatnipServices;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.util.Mth;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.neoforged.neoforge.fluids.FluidStack;
import petrolpark.mc.library.compat.create.shared.content.processing.meshBasin.FilteringRecipe;
import petrolpark.mc.library.compat.create.shared.registry.SharedCreateBlocks;
import petrolpark.mc.library.compat.jei.category.PetrolparkRecipeCategory;
import petrolpark.mc.library.shared.ISharedFeature;
import petrolpark.mc.library.shared.SharedFeatureFlag;

public class FilteringCategory extends BasinCategory implements ISharedFeature {

    private final AnimatedSpoutAndBasin spout = new AnimatedSpoutAndBasin();
    private final AnimatedBlazeBurner heater = new AnimatedBlazeBurner();

    public FilteringCategory(CreateRecipeCategory.Info<BasinRecipe> info, IJeiHelpers helpers) {
        super(info, true);
    };

    @Override
    public void setRecipe(@Nonnull IRecipeLayoutBuilder builder, @Nonnull RecipeHolder<BasinRecipe> holder, @Nonnull IFocusGroup focuses) {
        super.setRecipe(builder, holder, focuses);

        if (holder.value() instanceof FilteringRecipe filteringRecipe) addFluidSlot(builder,72, 16, filteringRecipe.getInputFluid()); //TODO position properly

        PetrolparkRecipeCategory.addOptionalRequiredBiomeSlot(builder, holder.value(), 3, 3);
        PetrolparkRecipeCategory.addOptionalRecipeBookSlot(getRecipeType(), builder, holder, 22, 3);
    };

    @Override
    public void draw(@Nonnull BasinRecipe recipe, @Nonnull IRecipeSlotsView recipeSlotsView, @Nonnull GuiGraphics graphics, double mouseX, double mouseY) {
        super.draw(recipe, recipeSlotsView, graphics, mouseX, mouseY);

        spout.withFluids(recipe instanceof FilteringRecipe filteringRecipe ? List.of(filteringRecipe.getInputFluid().getFluids()) : Collections.emptyList())
            .draw(graphics, getBackground().getWidth() / 2 + 3, 34);

        final HeatCondition requiredHeat = recipe.getRequiredHeat();
		if (requiredHeat != HeatCondition.NONE) heater.withHeat(requiredHeat.visualizeAsBlazeBurner())
		    .draw(graphics, getBackground().getWidth() / 2 + 3, 55);
    };

    /**
     * Copied from {@link AnimatedSpout Create source code}
     */
    class AnimatedSpoutAndBasin extends AnimatedKinetics {

        private List<FluidStack> fluids;

        public AnimatedSpoutAndBasin withFluids(List<FluidStack> fluids) {
		    this.fluids = fluids;
		    return this;
	    };

        @Override
        public void draw(@Nonnull GuiGraphics graphics, int xOffset, int yOffset) {
            final PoseStack matrixStack = graphics.pose();
            matrixStack.pushPose();
            matrixStack.translate(xOffset, yOffset, 200);
            matrixStack.mulPose(Axis.XP.rotationDegrees(-15.5f));
            matrixStack.mulPose(Axis.YP.rotationDegrees(22.5f));
            int scale = 23;

            blockElement(AllBlocks.SPOUT.getDefaultState())
			.scale(scale)
			.render(graphics);

            float cycle = (AnimationTickHolder.getRenderTime() - offset * 8) % 30;
            float squeeze = cycle < 20 ? Mth.sin((float) (cycle / 20f * Math.PI)) : 0;
            squeeze *= 20;

            matrixStack.pushPose();

            blockElement(AllPartialModels.SPOUT_TOP)
                .scale(scale)
                .render(graphics);
            matrixStack.translate(0, -3 * squeeze / 32f, 0);
            blockElement(AllPartialModels.SPOUT_MIDDLE)
                .scale(scale)
                .render(graphics);
            matrixStack.translate(0, -3 * squeeze / 32f, 0);
            blockElement(AllPartialModels.SPOUT_BOTTOM)
                .scale(scale)
                .render(graphics);
            matrixStack.translate(0, -3 * squeeze / 32f, 0);

            matrixStack.popPose();

            blockElement(SharedCreateBlocks.MESH_BASIN.getDefaultState())
                .atLocal(0, 1.65, 0)
                .scale(scale)
                .render(graphics);

            AnimatedKinetics.DEFAULT_LIGHTING.applyLighting();
            matrixStack.pushPose();
            UIRenderHelper.flipForGuiRender(matrixStack);
            matrixStack.scale(16, 16, 16);
            float from = 3f / 16f;
            float to = 17f / 16f;
            FluidStack fluidStack = fluids.get(0);
            NeoForgeCatnipServices.FLUID_RENDERER.renderFluidBox(fluidStack, from, from, from, to, to, to, graphics.bufferSource(), matrixStack, LightTexture.FULL_BRIGHT, false, true);
            matrixStack.popPose();

            float width = 1 / 128f * squeeze;
            matrixStack.translate(scale / 2f, scale * 1.5f, scale / 2f);
            UIRenderHelper.flipForGuiRender(matrixStack);
            matrixStack.scale(16, 16, 16);
            matrixStack.translate(-0.5f, 0, -0.5f);
            from = -width / 2 + 0.5f;
            to = width / 2 + 0.5f;
            NeoForgeCatnipServices.FLUID_RENDERER.renderFluidBox(fluidStack, from, 0, from, to, 2, to, graphics.bufferSource(), matrixStack, LightTexture.FULL_BRIGHT, false, true);
            graphics.flush();
            Lighting.setupFor3DItems();

            matrixStack.popPose();
        };

    };

    @Override
    public SharedFeatureFlag getSharedFeatureFlag() {
        return SharedFeatureFlag.MESH_BASIN;
    };
};
