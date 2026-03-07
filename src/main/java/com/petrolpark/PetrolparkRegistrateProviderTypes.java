package com.petrolpark;

import com.tterrag.registrate.providers.ProviderType;
import com.tterrag.registrate.providers.RegistrateTagsProvider;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class PetrolparkRegistrateProviderTypes {
    
    @SuppressWarnings("null")
    public static final ProviderType<RegistrateTagsProvider.IntrinsicImpl<BlockEntityType<?>>> BLOCK_ENTITY_TYPE_TAGS = ProviderType.registerIntrinsicTag("tags/block_entity_type", "block_entity_types", Registries.BLOCK_ENTITY_TYPE, type -> type.builtInRegistryHolder().getKey());
    public static final ProviderType<RegistrateTagsProvider.IntrinsicImpl<MobEffect>> MOB_EFFECT_TAGS = ProviderType.registerIntrinsicTag("tags/mob_effect", "mob_effects", Registries.MOB_EFFECT, effect -> PetrolparkRegistries.getHolder(BuiltInRegistries.MOB_EFFECT, effect).map(Holder.Reference::key).orElseThrow());
    public static final ProviderType<RegistrateTagsProvider.IntrinsicImpl<Potion>> POTION_TAGS = ProviderType.registerIntrinsicTag("tags/potion", "potions", Registries.POTION, potion -> PetrolparkRegistries.getHolder(BuiltInRegistries.POTION, potion).map(Holder.Reference::key).orElseThrow());

    public static final void register() {};
};
