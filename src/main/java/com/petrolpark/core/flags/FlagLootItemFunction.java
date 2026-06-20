package com.petrolpark.core.flags;

import java.util.Locale;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.petrolpark.registry.PetrolparkLootItemFunctions;

import net.createmod.catnip.codecs.stream.CatnipStreamCodecBuilders;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;

//TODO change to conditional loot function
public record FlagLootItemFunction(Holder<Flag> flagHolder, Action action) implements LootItemFunction {

    public static final MapCodec<FlagLootItemFunction> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        Flag.CODEC.fieldOf("flag").forGetter(FlagLootItemFunction::flagHolder),
        Action.CODEC.optionalFieldOf("action", Action.ADD).forGetter(FlagLootItemFunction::action)
    ).apply(instance, FlagLootItemFunction::new));

    @Override
    public ItemStack apply(ItemStack stack, LootContext context) {
        action().apply(ItemFlagPole.get(stack), flagHolder());
        return stack;
    };

    @Override
    public LootItemFunctionType<? extends LootItemFunction> getType() {
        return PetrolparkLootItemFunctions.FLAG.get();
    };

    public static enum Action implements StringRepresentable {

        ADD {
            @Override
            public void apply(IFlagPole<?, ?> flags, Holder<Flag> flagHolder) {
                flags.flag(flagHolder);
            };
        },
        REMOVE {
            @Override
            public void apply(IFlagPole<?, ?> flags, Holder<Flag> flagHolder) {
                flags.unflag(flagHolder);
            };
        },
        REMOVE_ONLY {
            @Override
            public void apply(IFlagPole<?, ?> flags, Holder<Flag> flagHolder) {
                flags.unflagOnly(flagHolder);
            };
        };

        
        public static final Codec<Action> CODEC = StringRepresentable.fromEnum(Action::values);
        public static final StreamCodec<RegistryFriendlyByteBuf, Action> STREAM_CODEC = CatnipStreamCodecBuilders.ofEnum(Action.class);

        public abstract void apply(IFlagPole<?, ?> flags, Holder<Flag> flagHolder);

        @Override
        public String getSerializedName() {
            return name().toLowerCase(Locale.ROOT);
        };
    };
    
};
