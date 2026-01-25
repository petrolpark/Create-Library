package com.petrolpark.compat;

import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import com.google.common.base.Strings;
import com.petrolpark.PetrolparkRegistrate;
import com.petrolpark.util.Lang;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StringRepresentable;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.neoforgespi.language.IModInfo;

public enum Mods implements StringRepresentable {

    CREATE,

    // Petrolpark Mods
    PETROLPARK,
    CREATE_BISTRO("createbistro"),
    DESTROY,
    PETROLS_PARTS("petrolsparts"),
    PQUALITY,

    // Others
    ARS_NOUVEAU("ars_nouveau"),
    BIG_CANNONS("createbigcannons"),
    BREWIN_AND_CHEWIN("brewinandchewin"),
    CURIOS,
    JEI,
    TFMG,
    YOUKAIS_HOMECOMING("youkaishomecoming"),
    ;

    public static final Function<String, Mods> LOOKUP = StringRepresentable.createNameLookup(values(), Function.identity());

    public final String id;
    private PetrolparkRegistrate registrate;

    private Mods() {
        id = Lang.asId(name());
    };

    private Mods(String id) {
        this.id = id;
    };

    public String getId() {
        return id;
    };

    public String getName() {
        return ModList.get().getModContainerById(id).map(ModContainer::getModInfo).map(IModInfo::getDisplayName).filter(Predicate.not(Strings::isNullOrEmpty)).orElse("Unknown");
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

    @Override
    public String getSerializedName() {
        return id;
    };
};
