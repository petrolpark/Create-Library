package com.petrolpark.compat;

import java.util.function.Supplier;

import com.petrolpark.registrate.PetrolparkRegistrate;
import com.simibubi.create.foundation.utility.Lang;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLLoader;

public enum CompatMods {

    // Petrolpark Mods
    DESTROY,
    PETROLS_PARTS("petrolsparts"),
    CHOO_CHOO_TRADE("choochootrade"),

    // Others
    BIG_CANNONS("createbigcannons"),
    CURIOS,
    JEI,
    TFMG;

    public final String id;
    private PetrolparkRegistrate registrate;

    private CompatMods() {
        id = Lang.asId(name());
    };

    private CompatMods(String id) {
        this.id = id;
    };

    public boolean isLoading() {
        return FMLLoader.getLoadingModList().getModFileById(id) != null;
    };

    public boolean isLoaded() {
		return ModList.get().isLoaded(id);
	};

    public void executeIfInstalled(Supplier<Runnable> toExecute) {
		if (isLoaded()) toExecute.get().run();
	};

    public ResourceLocation asResource(String path) {
        return new ResourceLocation(id, path);
    };

    public PetrolparkRegistrate registrate() {
        if (registrate == null) registrate = new PetrolparkRegistrate(id);
        return registrate;
    };
};
