package com.petrolpark.core.contamination;

import javax.annotation.Nonnull;

import org.jetbrains.annotations.NotNull;

import com.google.gson.JsonSyntaxException;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.resources.ResourceLocation;
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
        .and(ResourceLocation.CODEC.fieldOf("contaminant").forGetter(cglm -> cglm.contaminantLocation))
        .and(NumberProviders.CODEC.fieldOf("chance").forGetter(ContaminateGlobalLootModifier::getChanceProvider))
        .apply(instance, ContaminateGlobalLootModifier::new)
    );

    private final ResourceLocation contaminantLocation;
    protected Contaminant contaminant;
    protected final NumberProvider chanceProvider;

    protected ContaminateGlobalLootModifier(LootItemCondition[] conditionsIn, ResourceLocation contaminantLocation, NumberProvider chanceProvider) {
        super(conditionsIn);
        this.contaminantLocation = contaminantLocation;
        this.chanceProvider = chanceProvider;
    };

    public NumberProvider getChanceProvider() {
        return chanceProvider;
    };

    public Contaminant getContaminant() {
        if (contaminant == null) contaminant = Contaminant.get(contaminantLocation);
        return contaminant;
    };

    @Override
    public MapCodec<? extends IGlobalLootModifier> codec() {
        return CODEC;
    };

    @Override
    protected @NotNull ObjectArrayList<ItemStack> doApply(@Nonnull ObjectArrayList<ItemStack> generatedLoot, @Nonnull LootContext context) {
        if (getContaminant() == null) throw new JsonSyntaxException("Unknown Contaminant in contaminate Global Loot Modifier: "+contaminantLocation.toString());
        float chance = chanceProvider.getFloat(context);
        if (chance <= 0f) return generatedLoot;
        for (ItemStack stack : generatedLoot) {
            if (context.getRandom().nextFloat() > chance) continue;
            ItemContamination.get(stack).contaminate(context.getLevel().registryAccess(), getContaminant());
        };
        return generatedLoot;
    };
    
};
