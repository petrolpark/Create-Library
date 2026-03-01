package com.petrolpark.compat.create;

import java.util.function.Supplier;

import org.jetbrains.annotations.ApiStatus.Internal;

import com.petrolpark.Petrolpark;
import com.petrolpark.core.data.ResourceLocationSet;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public class PetrolparkCreateAttachmentTypes {
    private static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, Petrolpark.MOD_ID);

	public static final Supplier<AttachmentType<ResourceLocationSet>> FTL_RECIPES = ATTACHMENT_TYPES.register(
		"first_time_lucky_recipes", AttachmentType.builder(ResourceLocationSet::new).serialize(ResourceLocationSet.CODEC).copyOnDeath()::build
	);

	@Internal
	public static void register(IEventBus modEventBus) {
		ATTACHMENT_TYPES.register(modEventBus);
	};
};
