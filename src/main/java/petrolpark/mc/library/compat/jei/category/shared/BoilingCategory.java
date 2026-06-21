package petrolpark.mc.library.compat.jei.category.shared;

import javax.annotation.Nonnull;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import petrolpark.mc.library.compat.create.shared.content.processing.meshBasin.BoilingRecipe;
import petrolpark.mc.library.compat.create.shared.registry.SharedCreateBlocks;
import petrolpark.mc.library.compat.jei.category.SmallBasinCategory;
import petrolpark.mc.library.shared.ISharedFeature;
import petrolpark.mc.library.shared.SharedFeatureFlag;
import com.simibubi.create.compat.jei.category.animations.AnimatedKinetics;

import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IJeiHelpers;
import net.minecraft.client.gui.GuiGraphics;

public class BoilingCategory extends SmallBasinCategory<BoilingRecipe> implements ISharedFeature {

    private final AnimatedMeshBasin basin = new AnimatedMeshBasin();

    public BoilingCategory(Info<BoilingRecipe> info, IJeiHelpers helpers) {
        super(info, helpers);
    };

    @Override
    protected void draw(@Nonnull BoilingRecipe recipe, @Nonnull IRecipeSlotsView recipeSlotsView, @Nonnull GuiGraphics graphics, double mouseX, double mouseY) {
        super.draw(recipe, recipeSlotsView, graphics, mouseX, mouseY);

        basin.draw(graphics, getBackground().getWidth() / 2 + 3, 49);
    };

    class AnimatedMeshBasin extends AnimatedKinetics {

        @Override
        public void draw(@Nonnull GuiGraphics graphics, int xOffset, int yOffset) {
            PoseStack matrixStack = graphics.pose();
            matrixStack.pushPose();
            matrixStack.translate(xOffset, yOffset, 200);
            matrixStack.mulPose(Axis.XP.rotationDegrees(-15.5f));
            matrixStack.mulPose(Axis.YP.rotationDegrees(22.5f));
            int scale = 23;

            blockElement(SharedCreateBlocks.MESH_BASIN.getDefaultState())
                .atLocal(0, 0, 0)
                .scale(scale)
                .render(graphics);

            matrixStack.popPose();;
        };

    };

    @Override
    public SharedFeatureFlag getSharedFeatureFlag() {
        return SharedFeatureFlag.MESH_BASIN;
    };
    
};
