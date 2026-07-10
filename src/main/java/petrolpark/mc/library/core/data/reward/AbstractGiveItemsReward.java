package petrolpark.mc.library.core.data.reward;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

import javax.annotation.ParametersAreNonnullByDefault;

import com.mojang.datafixers.Products;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctions;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.neoforged.neoforge.common.conditions.ConditionalOps;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import petrolpark.mc.library.registry.PetrolparkLootContextParams;

@ParametersAreNonnullByDefault
public abstract class AbstractGiveItemsReward implements IReward {

    protected static <REWARD extends AbstractGiveItemsReward> Products.P1<RecordCodecBuilder.Mu<REWARD>, List<LootItemFunction>> lateItemFunctionsField(RecordCodecBuilder.Instance<REWARD> instance) {
        return instance.group(ConditionalOps.decodeListWithElementConditions(LootItemFunctions.ROOT_CODEC).optionalFieldOf("functions", Collections.emptyList()).forGetter(AbstractGiveItemsReward::itemFunctions));
    };

    /**
     * Functions to apply <b>at the point at which the Items are given to the Entity</b>.
     */
    private final List<LootItemFunction> functions;
    protected final BiFunction<ItemStack, LootContext, ItemStack> compositeFunction;

    public AbstractGiveItemsReward(List<LootItemFunction> functions) {
        this.functions = functions;
        compositeFunction = LootItemFunctions.compose(functions);
    };

    public final List<LootItemFunction> itemFunctions() {
        return functions;
    };

    public abstract Stream<ItemStack> streamStacks(LootContext context);

    @Override
    public final boolean reward(LootContext context, float multiplier, boolean simulate) {
        final IItemHandler handler = context.getParamOrNull(PetrolparkLootContextParams.ITEM_HANDLER);
        if (handler == null) return false;
        return streamStacks(context)
            .map(multiplyAmount(multiplier))
            .map(stack -> compositeFunction.apply(stack, context))
            .allMatch(stack -> ItemHandlerHelper.insertItemStacked(handler, stack, simulate).isEmpty());
    };

    private UnaryOperator<ItemStack> multiplyAmount(float multiplier) {
        return stack -> stack.copyWithCount(Math.min(stack.getMaxStackSize(), (int)(multiplier * (float)stack.getCount())));
    };

    @Override
    public Set<LootContextParam<?>> getReferencedContextParams() {
        return Collections.singleton(PetrolparkLootContextParams.ITEM_HANDLER);
    };

    @Override
    public void validate(ValidationContext context) {
        IReward.super.validate(context);
        //TODO validate with rewards param set
        for (int i = 0; i < itemFunctions().size(); i++) itemFunctions().get(i).validate(context.forChild("function[" + i + "]"));
    };
    
};
