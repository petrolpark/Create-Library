package com.petrolpark.core.contamination;

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

public class ContaminateGlobalLootModifier extends LootModifier {

    public static final MapCodec<ContaminateGlobalLootModifier> CODEC = RecordCodecBuilder.mapCodec(instance -> 
        codecStart(instance)
        .and(Contaminant.CODEC.fieldOf("contaminant").forGetter(ContaminateGlobalLootModifier::getContaminant))
        .and(NumberProviders.CODEC.fieldOf("chance").forGetter(ContaminateGlobalLootModifier::getChanceProvider))
        .apply(instance, ContaminateGlobalLootModifier::new)
    );

    protected final Holder<Contaminant> contaminant;
    protected final NumberProvider chanceProvider;

    protected ContaminateGlobalLootModifier(LootItemCondition[] conditionsIn, Holder<Contaminant> contaminant, NumberProvider chanceProvider) {
        super(conditionsIn);
        this.contaminant = contaminant;
        this.chanceProvider = chanceProvider;
    };

    public NumberProvider getChanceProvider() {
        return chanceProvider;
    };

    public Holder<Contaminant> getContaminant() {
        return contaminant;
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
            ItemContamination.get(stack).contaminate(getContaminant());
        };
        return generatedLoot;
    };
    
};
