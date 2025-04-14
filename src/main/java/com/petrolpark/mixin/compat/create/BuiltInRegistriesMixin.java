package com.petrolpark.mixin.compat.create;

import org.spongepowered.asm.mixin.Mixin;

import com.petrolpark.compat.create.CreateRegistries;

import net.minecraft.core.registries.BuiltInRegistries;

@Mixin(BuiltInRegistries.class)
public class BuiltInRegistriesMixin {

	static {
		CreateRegistries.init();
	};
};

