package petrolpark.mc.library.core.data.loot.modifier;

import com.mojang.serialization.Codec;
import petrolpark.mc.library.registry.PetrolparkRegistries;

import net.minecraft.core.HolderLookup;
import net.minecraft.world.level.storage.loot.LootTable;

public interface ILootTableModifier extends ILootModifierBase {

    /**
     * Use {@link ILootTableModifier#CODEC instead}.
     */
    static final Codec<ILootTableModifier> TYPED_CODEC = PetrolparkRegistries.LOOT_TABLE_MODIFIER_TYPES
        .byNameCodec()
        .dispatch(ILootTableModifier::getType, LootTableModifierType::codec);

    public static final Codec<ILootTableModifier> CODEC = Codec.lazyInitialized(() -> TYPED_CODEC);
    
    public LootTable modify(HolderLookup.Provider registries, LootTable originalTable);

    public LootTableModifierType getType();
};
