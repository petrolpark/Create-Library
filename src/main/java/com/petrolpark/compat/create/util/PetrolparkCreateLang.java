package com.petrolpark.compat.create.util;

import com.petrolpark.Petrolpark;

import net.createmod.catnip.lang.LangBuilder;

public class PetrolparkCreateLang {
    
    public static final LangBuilder builder() {
		return new LangBuilder(Petrolpark.MOD_ID);
	};

    public static final LangBuilder translate(String langKey, Object... args) {
		return builder().translate(langKey, args);
	};
};
