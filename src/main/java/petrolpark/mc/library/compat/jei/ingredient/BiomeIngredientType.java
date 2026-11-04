package petrolpark.mc.library.compat.jei.ingredient;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import javax.annotation.Nonnull;

import mezz.jei.api.ingredients.IIngredientRenderer;
import mezz.jei.api.ingredients.IIngredientType;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.biome.Biome;
import petrolpark.mc.library.compat.jei.BiomeIconLayer;
import petrolpark.mc.library.compat.jei.JEITextureDrawable;
import petrolpark.mc.library.compat.jei.ingredient.BiomeIngredientType.BiomeHolderHolder;
import petrolpark.mc.library.core.client.rendering.PetrolparkGuiTexture;
import petrolpark.mc.library.registry.PetrolparkDataMapTypes;

/**
 * {@link Biome} {@link IIngredientType}
 */
public class BiomeIngredientType implements IIngredientType<BiomeHolderHolder> {

    public static final BiomeIngredientType TYPE = new BiomeIngredientType();
    public static final HolderIngredientHelper<Biome, BiomeHolderHolder> HELPER = new HolderIngredientHelper<>(TYPE, Registries.BIOME, BiomeHolderHolder::new, "biome");
    public static final Renderer RENDERER = new BiomeIngredientType.Renderer();

    @Override
    public Class<BiomeHolderHolder> getIngredientClass() {
        return BiomeHolderHolder.class;
    };

    public record BiomeHolderHolder(Holder<Biome> holder) implements HolderIngredientHelper.HolderHolder<Biome> {};

    public static class Renderer implements IIngredientRenderer<BiomeHolderHolder> {

        private final JEITextureDrawable globe = JEITextureDrawable.of(PetrolparkGuiTexture.JEI_GLOBE);

        @Override
        public void render(@Nonnull GuiGraphics guiGraphics, @Nonnull BiomeHolderHolder ingredient) {
            for (BiomeIconLayer layer : Optional.ofNullable(ingredient.holder().getData(PetrolparkDataMapTypes.BIOME_ICON))
                .orElse(BiomeIconLayer.DEFAULT)
            ) {
                layer.color().map(Function.identity(), color -> color.colorGetter.apply(ingredient.value())).ifPresentOrElse(
                    color -> {

                    },
                    () -> guiGraphics.blit(0, 0, 0, 16, 16, Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(layer.texture()))
                );
            };
        };

        @Override
        public List<Component> getTooltip(@Nonnull BiomeHolderHolder ingredient, @Nonnull TooltipFlag tooltipFlag) {
            ResourceLocation rl = HELPER.getResourceLocation(ingredient);
            if (rl == null) return Collections.emptyList();
            List<Component> tooltip = new ArrayList<>(tooltipFlag.isAdvanced() ? 2 : 1);
            tooltip.add(HELPER.getDisplayNameComponent(ingredient));
            if (tooltipFlag.isAdvanced()) tooltip.add(Component.literal(rl.toString()).withStyle(ChatFormatting.DARK_GRAY));
            return tooltip;
        };

    };
    
};
