package petrolpark.mc.library.compat.create.shared.content.processing.centrifuge;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import petrolpark.mc.library.util.AdvancementHelper;

import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.FluidIngredient;

public class CentrifugeCriterionTrigger extends SimpleCriterionTrigger<CentrifugeCriterionTrigger.Instance> {
    
    public void trigger(ServerPlayer player, List<ItemStack> items, FluidStack denseOutputStack, FluidStack lightOutputStack) {
        trigger(player, instance -> instance.matches(items, denseOutputStack, lightOutputStack));
    };

    public Consumer<ServerPlayer> trigger(List<ItemStack> items, FluidStack denseOutputStack, FluidStack lightOutputStack) {
        return player -> trigger(player, items, denseOutputStack, lightOutputStack);
    };

    @Override
    public Codec<CentrifugeCriterionTrigger.Instance> codec() {
        return CentrifugeCriterionTrigger.Instance.CODEC;
    };

    public static record Instance(
        Optional<ContextAwarePredicate> player,
        List<ItemPredicate> items,
        Optional<FluidIngredient> denseOutputFluid,
        Optional<FluidIngredient> lightOutputFluid
    ) implements SimpleCriterionTrigger.SimpleInstance {

        public static final Codec<CentrifugeCriterionTrigger.Instance> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter(CentrifugeCriterionTrigger.Instance::player),
            ItemPredicate.CODEC.listOf().optionalFieldOf("output_items", Collections.emptyList()).forGetter(CentrifugeCriterionTrigger.Instance::items),
            FluidIngredient.CODEC.optionalFieldOf("dense_output_fluid").forGetter(CentrifugeCriterionTrigger.Instance::denseOutputFluid),
            FluidIngredient.CODEC.optionalFieldOf("light_output_fluid").forGetter(CentrifugeCriterionTrigger.Instance::lightOutputFluid)
        ).apply(instance, CentrifugeCriterionTrigger.Instance::new));

        boolean matches(List<ItemStack> items, FluidStack denseOutputStack, FluidStack lightOutputStack) {
            return AdvancementHelper.testItems(items(), items)
                && AdvancementHelper.testFluid(denseOutputFluid(), denseOutputStack)
                && AdvancementHelper.testFluid(lightOutputFluid(), lightOutputStack);
        };
    };

};
