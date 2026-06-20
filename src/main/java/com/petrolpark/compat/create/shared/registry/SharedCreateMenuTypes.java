package com.petrolpark.compat.create.shared.registry;

import com.petrolpark.Petrolpark;
import com.petrolpark.compat.create.shared.content.redstone.programmer.RedstoneProgrammerMenu;
import com.petrolpark.compat.create.shared.content.redstone.programmer.RedstoneProgrammerScreen;
import com.tterrag.registrate.util.entry.MenuEntry;

public class SharedCreateMenuTypes {
    
    public static final MenuEntry<RedstoneProgrammerMenu> REDSTONE_PROGRAMMER = Petrolpark.REGISTRATE.menu("redstone_programmer", RedstoneProgrammerMenu::new, () -> RedstoneProgrammerScreen::new)
        .register();

    public static final void register() {};
};
