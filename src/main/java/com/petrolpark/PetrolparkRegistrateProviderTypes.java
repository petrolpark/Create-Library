package com.petrolpark;

import com.tterrag.registrate.providers.ProviderType;
import com.tterrag.registrate.providers.RegistrateTagsProvider;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class PetrolparkRegistrateProviderTypes {
    
    @SuppressWarnings("null")
    public static final ProviderType<RegistrateTagsProvider.IntrinsicImpl<BlockEntityType<?>>> BLOCK_ENTITY_TYPE_TAGS = ProviderType.registerIntrinsicTag("tags/block_entity_type", "block_entity_types", Registries.BLOCK_ENTITY_TYPE, type -> type.builtInRegistryHolder().getKey());

    public static final void register() {};
};
