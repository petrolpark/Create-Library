package com.petrolpark.core.data.reward.entity;

import java.util.List;
import java.util.stream.Stream;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.petrolpark.PetrolparkRewardTypes;
import com.petrolpark.util.Lang.IndentedTooltipBuilder;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;

/**
 * <p>{@code petrolpark:give_item}</p>
 * 
 * Give a single Item Stack to an Entity by putting it into its Inventory or dropping it.
 * 
 * Arguments:
 * <ul>
 * <li> {@code item} - {@link ItemStack} to give to the recipient
 * </ul>
 * 
 * @author petrolpark
 */
public class GiveItemEntityReward extends AbstractGiveItemsEntityReward {

    public static final MapCodec<GiveItemEntityReward> CODEC = RecordCodecBuilder.mapCodec(instance ->
        instance.group(
            ItemStack.CODEC.fieldOf("item").forGetter(GiveItemEntityReward::getStack)
        ).and(lateItemFunctionsField(instance).t1())
        .apply(instance, GiveItemEntityReward::new)
    );

    protected final ItemStack stack;

    public GiveItemEntityReward(ItemStack stack, List<LootItemFunction> functions) {
        super(functions);
        this.stack = stack;
    };

    public ItemStack getStack() {
        return stack;
    };

    @Override
    public Stream<ItemStack> streamStacks(Entity recipient, LootContext context) {
        return Stream.of(stack);
    };

    @Override
    public void render(GuiGraphics graphics) {
        graphics.renderItem(stack, 0, 0);
    };

    @Override
    public void addToDescription(IndentedTooltipBuilder builder) {
        builder.add(translateSimple(stack.getDisplayName()));
    };

    @Override
    public EntityRewardType getType() {
        return PetrolparkRewardTypes.GIVE_ITEM.get();
    };
    
};
