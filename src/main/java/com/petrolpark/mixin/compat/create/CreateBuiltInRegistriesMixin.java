package com.petrolpark.mixin.compat.create;

import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.core.registries.BuiltInRegistries;

@Mixin(BuiltInRegistries.class)
public class CreateBuiltInRegistriesMixin {

	static {
		//CreateRegistries.init();
	};
};

