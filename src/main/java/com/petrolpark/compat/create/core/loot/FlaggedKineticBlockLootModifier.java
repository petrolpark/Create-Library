package com.petrolpark.compat.create.core.loot;

import javax.annotation.Nonnull;

import org.jetbrains.annotations.NotNull;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.petrolpark.PetrolparkTags;
import com.petrolpark.RequiresCreate;
import com.petrolpark.compat.create.core.block.entity.behaviour.FlagPoleBehaviour;
import com.petrolpark.core.flags.ItemFlagPole;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.common.loot.LootModifier;

@RequiresCreate
public class FlaggedKineticBlockLootModifier extends LootModifier {

    public static final MapCodec<FlaggedKineticBlockLootModifier> CODEC = RecordCodecBuilder.mapCodec(inst -> codecStart(inst).apply(inst, FlaggedKineticBlockLootModifier::new));

    public FlaggedKineticBlockLootModifier() {
        this(new LootItemCondition[]{});
    };

    protected FlaggedKineticBlockLootModifier(LootItemCondition[] conditionsIn) {
        super(conditionsIn);
    };

    @Override
    public MapCodec<? extends IGlobalLootModifier> codec() {
        return CODEC;
    };

    @Override
    protected @NotNull ObjectArrayList<ItemStack> doApply(@Nonnull ObjectArrayList<ItemStack> generatedLoot, @Nonnull LootContext context) {
        final BlockEntity be = context.getParamOrNull(LootContextParams.BLOCK_ENTITY);
        if (be == null || !(be instanceof KineticBlockEntity kbe)) return generatedLoot;
        final Item item = kbe.getBlockState().getBlock().asItem();
        if (!PetrolparkTags.Items.FLAGGABLE.matches(item)) return generatedLoot;
        final FlagPoleBehaviour behaviour = kbe.getBehaviour(FlagPoleBehaviour.TYPE);
        if (behaviour == null) return generatedLoot;
        generatedLoot.stream().filter(stack -> stack.getItem() == item).map(ItemFlagPole::get).forEach(c -> c.flagAll(behaviour.getFlagPole().streamAllFlags()));
        return generatedLoot;
    };
    
};
