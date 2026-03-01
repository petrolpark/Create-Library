package com.petrolpark.compat.create;

import com.petrolpark.Petrolpark;
import com.petrolpark.compat.create.common.redstone.programmer.RedstoneProgrammerMenu;
import com.petrolpark.compat.create.common.redstone.programmer.RedstoneProgrammerScreen;
import com.tterrag.registrate.util.entry.MenuEntry;

public class PetrolparkCreateMenuTypes {
    
    public static final MenuEntry<RedstoneProgrammerMenu> REDSTONE_PROGRAMMER = Petrolpark.REGISTRATE.menu("redstone_programmer", RedstoneProgrammerMenu::new, () -> RedstoneProgrammerScreen::new)
        .register();

    public static final void register() {};
};
