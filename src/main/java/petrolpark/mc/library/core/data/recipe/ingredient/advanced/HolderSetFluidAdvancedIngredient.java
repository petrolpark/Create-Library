package petrolpark.mc.library.core.data.recipe.ingredient.advanced;

import com.mojang.serialization.MapCodec;
import petrolpark.mc.library.registry.PetrolparkAdvancedIngredientTypes;
import petrolpark.mc.library.util.Lang.IndentedTooltipBuilder;
import petrolpark.mc.library.util.codec.CodecHelper;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;

public record HolderSetFluidAdvancedIngredient(HolderSet<Fluid> set) implements FluidAdvancedIngredient {

    public static final MapCodec<HolderSetFluidAdvancedIngredient> CODEC = CodecHelper.singleFieldMap(RegistryCodecs.homogeneousList(Registries.FLUID), "set", HolderSetFluidAdvancedIngredient::set, HolderSetFluidAdvancedIngredient::new);
    public static final StreamCodec<RegistryFriendlyByteBuf, HolderSetFluidAdvancedIngredient> STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.holderSet(Registries.FLUID), HolderSetFluidAdvancedIngredient::set, HolderSetFluidAdvancedIngredient::new);

    @Override
    public boolean test(FluidStack stack) {
        return set.contains(stack.getFluidHolder());
    };

    @Override
    public void addToDescription(IndentedTooltipBuilder description) {
        set.unwrap().map(
            tag -> description.add(Component.translatable("advancedIngredient.petrolpark.tag", tag)),
            items -> {
                description.add(translate("")).indent();
                items.stream().map(Holder::value).map(Fluid::getFluidType).map(FluidType::getDescription).forEach(description::add);
                return description.unindent();
            }
        );
    };

    @Override
    public void addToCounterDescription(IndentedTooltipBuilder description) {
        set.unwrap().map(
            tag -> description.add(Component.translatable("advancedIngredient.petrolpark.tag.inverse", tag)),
            items -> {
                description.add(translateInverse()).indent();
                items.stream().map(Holder::value).map(Fluid::getFluidType).map(FluidType::getDescription).forEach(description::add);
                return description.unindent();
            }
        );
    };

    @Override
    public INamedAdvancedIngredientType<FluidStack> getType() {
        return PetrolparkAdvancedIngredientTypes.FLUID_HOLDER_SET.get();
    };
    
};
