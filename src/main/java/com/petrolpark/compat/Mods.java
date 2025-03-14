package com.petrolpark.compat;

import java.util.function.Supplier;

import com.petrolpark.registrate.PetrolparkRegistrate;
import com.petrolpark.util.Lang;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLLoader;

public enum Mods {

    CREATE,

    // Petrolpark Mods
    ACADEMY,
    CREATE_BISTRO("createbistro"),
    DESTROY,
    PETROLS_PARTS("petrolsparts"),
    PQUALITY,

    // Others
    BIG_CANNONS("createbigcannons"),
    CURIOS,
    JEI,
    TFMG;

    public final String id;
    private PetrolparkRegistrate registrate;

    private Mods() {
        id = Lang.asId(name());
    };

    private Mods(String id) {
        this.id = id;
    };

    public static boolean isLoading(String modid) {
        return FMLLoader.getLoadingModList().getModFileById(modid) != null;
    };

    public boolean isLoading() {
       return isLoading(id);
    };

    public boolean isLoaded() {
		return ModList.get().isLoaded(id);
	};

    public void executeIfInstalled(Supplier<Runnable> toExecute) {
		if (isLoaded()) toExecute.get().run();
	};

    public ResourceLocation asResource(String path) {
        return ResourceLocation.fromNamespaceAndPath(id, path);
    };

    public PetrolparkRegistrate registrate() {
        if (registrate == null) registrate = new PetrolparkRegistrate(id);
        return registrate;
    };
};
