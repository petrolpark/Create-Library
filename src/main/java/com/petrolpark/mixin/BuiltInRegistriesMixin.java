package com.petrolpark.mixin;

import java.util.function.Consumer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.petrolpark.Petrolpark;
import com.petrolpark.PetrolparkRegistries;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;

@Mixin(BuiltInRegistries.class)
public class BuiltInRegistriesMixin {

	static {
		PetrolparkRegistries.init();
	};

	@WrapOperation(
        method = "validate",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/core/Registry;forEach(Ljava/util/function/Consumer;)V"
        )
    )
	private static <T extends Registry<?>> void wrapValidate(Registry<T> instance, Consumer<T> consumer, Operation<Void> original) {
		Consumer<T> callback = (t) -> {
			if (!t.key().location().getNamespace().equals(Petrolpark.MOD_ID))
				consumer.accept(t);
		};

		original.call(instance, callback);
	};
};

