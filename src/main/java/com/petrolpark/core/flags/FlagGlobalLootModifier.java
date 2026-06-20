package com.petrolpark.core.flags;

import javax.annotation.Nonnull;

import org.jetbrains.annotations.NotNull;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.Holder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.NumberProviders;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.common.loot.LootModifier;

public class FlagGlobalLootModifier extends LootModifier {

    public static final MapCodec<FlagGlobalLootModifier> CODEC = RecordCodecBuilder.mapCodec(instance -> 
        codecStart(instance)
        .and(Flag.CODEC.fieldOf("flag").forGetter(FlagGlobalLootModifier::getFlag))
        .and(NumberProviders.CODEC.fieldOf("chance").forGetter(FlagGlobalLootModifier::getChanceProvider))
        .apply(instance, FlagGlobalLootModifier::new)
    );

    protected final Holder<Flag> flag;
    protected final NumberProvider chanceProvider;

    protected FlagGlobalLootModifier(LootItemCondition[] conditionsIn, Holder<Flag> flag, NumberProvider chanceProvider) {
        super(conditionsIn);
        this.flag = flag;
        this.chanceProvider = chanceProvider;
    };

    public NumberProvider getChanceProvider() {
        return chanceProvider;
    };

    public Holder<Flag> getFlag() {
        return flag;
    };

    @Override
    public MapCodec<? extends IGlobalLootModifier> codec() {
        return CODEC;
    };

    @Override
    protected @NotNull ObjectArrayList<ItemStack> doApply(@Nonnull ObjectArrayList<ItemStack> generatedLoot, @Nonnull LootContext context) {
        float chance = chanceProvider.getFloat(context);
        if (chance <= 0f) return generatedLoot;
        for (ItemStack stack : generatedLoot) {
            if (context.getRandom().nextFloat() > chance) continue;
            ItemFlagPole.get(stack).flag(getFlag());
        };
        return generatedLoot;
    };
    
};
