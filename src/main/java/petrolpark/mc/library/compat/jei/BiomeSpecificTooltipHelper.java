package petrolpark.mc.library.compat.jei;

import java.util.List;
import java.util.stream.Stream;

import petrolpark.mc.library.core.data.recipe.IBiomeSpecificRecipe;
import petrolpark.mc.library.util.Lang;

import mezz.jei.api.gui.ingredient.IRecipeSlotRichTooltipCallback;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;

public class BiomeSpecificTooltipHelper {

    public static final Stream<Holder<Biome>> streamAllBiomes(IBiomeSpecificRecipe recipe) {
        Minecraft minecraft = Minecraft.getInstance();
        ClientLevel level = minecraft.level;
        if (level == null) return Stream.empty();
        return recipe.getAllowedBiomes().stream().flatMap(HolderSet::stream);
    };
    
    public static final IRecipeSlotRichTooltipCallback getAllowedBiomeList(IBiomeSpecificRecipe recipe) {
        Minecraft minecraft = Minecraft.getInstance();
        ClientLevel level = minecraft.level;
        if (level == null) return (view, tooltip) -> {};
        List<ResourceLocation> biomes = streamAllBiomes(recipe).map(Holder::getKey).map(ResourceKey::location).toList();
        return (view, tooltip) -> {
            if (!biomes.isEmpty()) tooltip.add(Component.EMPTY);
            tooltip.add(Lang.translate("recipe.biome_specific").withStyle(ChatFormatting.WHITE));
            biomes.forEach(biome -> tooltip.add(Component.translatable(biome.toLanguageKey("biome")).withStyle(ChatFormatting.GRAY)));
        };
    };
};
