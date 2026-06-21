package petrolpark.mc.library.core.data.reward.entity;

import java.util.Collections;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

import com.mojang.datafixers.Products;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import petrolpark.mc.library.util.ItemHelper;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctions;
import net.neoforged.neoforge.common.conditions.ConditionalOps;

public abstract class AbstractGiveItemsEntityReward implements IEntityReward {

    protected static <REWARD extends AbstractGiveItemsEntityReward> Products.P1<RecordCodecBuilder.Mu<REWARD>, List<LootItemFunction>> lateItemFunctionsField(RecordCodecBuilder.Instance<REWARD> instance) {
        return instance.group(ConditionalOps.decodeListWithElementConditions(LootItemFunctions.ROOT_CODEC).optionalFieldOf("functions", Collections.emptyList()).forGetter(AbstractGiveItemsEntityReward::getItemFunctions));
    };

    /**
     * Functions to apply <b>at the point at which the Items are given to the Entity</b>.
     */
    private final List<LootItemFunction> functions;
    protected final BiFunction<ItemStack, LootContext, ItemStack> compositeFunction;

    public AbstractGiveItemsEntityReward(List<LootItemFunction> functions) {
        this.functions = functions;
        compositeFunction = LootItemFunctions.compose(functions);
    };

    public final List<LootItemFunction> getItemFunctions() {
        return functions;
    };

    public abstract Stream<ItemStack> streamStacks(Entity recipient, LootContext context);

    @Override
    public final void reward(Entity entity, LootContext context, float multiplier) {
        ItemHelper.give(entity, streamStacks(entity, context)
            .map(stack -> compositeFunction.apply(stack, context))
            .map(multiplyAmount(multiplier))
        );
    };

    private UnaryOperator<ItemStack> multiplyAmount(float multiplier) {
        return stack -> stack.copyWithCount(Math.min(stack.getMaxStackSize(), (int)(multiplier * (float)stack.getCount())));
    };
    
};
