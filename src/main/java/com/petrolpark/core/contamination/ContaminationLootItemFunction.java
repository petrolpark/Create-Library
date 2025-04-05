package com.petrolpark.core.contamination;

import java.util.Locale;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.petrolpark.PetrolparkLootItemFunctions;

import net.createmod.catnip.codecs.stream.CatnipStreamCodecBuilders;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;

public record ContaminationLootItemFunction(Holder<Contaminant> contaminantHolder, Action action) implements LootItemFunction {

    public static final MapCodec<ContaminationLootItemFunction> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        Contaminant.CODEC.fieldOf("contaminant").forGetter(ContaminationLootItemFunction::contaminantHolder),
        Action.CODEC.optionalFieldOf("action", Action.ADD).forGetter(ContaminationLootItemFunction::action)
    ).apply(instance, ContaminationLootItemFunction::new));

    @Override
    public ItemStack apply(ItemStack stack, LootContext context) {
        action().apply(ItemContamination.get(stack), contaminantHolder());
        return stack;
    };

    @Override
    public LootItemFunctionType<? extends LootItemFunction> getType() {
        return PetrolparkLootItemFunctions.CONTAMINATION.get();
    };

    public static enum Action implements StringRepresentable {

        ADD {
            @Override
            public void apply(IContamination<?, ?> contamination, Holder<Contaminant> contaminantHolder) {
                contamination.contaminate(contaminantHolder);
            };
        },
        REMOVE {
            @Override
            public void apply(IContamination<?, ?> contamination, Holder<Contaminant> contaminantHolder) {
                contamination.decontaminate(contaminantHolder);
            };
        },
        REMOVE_ONLY {
            @Override
            public void apply(IContamination<?, ?> contamination, Holder<Contaminant> contaminantHolder) {
                contamination.decontaminateOnly(contaminantHolder);
            };
        };

        
        public static final Codec<Action> CODEC = StringRepresentable.fromEnum(Action::values);
        public static final StreamCodec<RegistryFriendlyByteBuf, Action> STREAM_CODEC = CatnipStreamCodecBuilders.ofEnum(Action.class);

        public abstract void apply(IContamination<?, ?> contamination, Holder<Contaminant> contaminantHolder);

        @Override
        public String getSerializedName() {
            return name().toLowerCase(Locale.ROOT);
        };
    };
    
};
