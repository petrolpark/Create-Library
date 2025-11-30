package com.petrolpark.config;

import com.petrolpark.core.inventory.extended.ExtendedInventoryClientHandler.ExtraHotbarSlotLocations;

import net.createmod.catnip.config.ConfigBase;

public class PetrolparkClientConfig extends ConfigBase {

    public final ConfigGroup extraInventory = group(0, "extraInventory");
    public final ConfigBool extraInventoryLeft = b(true, "extraHotbarLeft", "Whether the extra slots render to the left rather than right of containers");
    public final ConfigInt extraInventoryWidth = i(3, 1, Integer.MAX_VALUE, "extraInventoryWidth", "Maximum width of extra inventory space");
    public final ConfigEnum<ExtraHotbarSlotLocations> extraHotbarSlotLocations = e(ExtraHotbarSlotLocations.ALL_LEFT, "extraHotbarSlotLocation", "Where the additional hotbar slots go", "ALL_X - All slots on X", "START_X - Alternate sides, starting on X", "PRIORITIZE_X - Up to extraHotbarPrioritySlotCount on X, the rest on the other side");
    public final ConfigInt extraHotbarPrioritySlotCount = i(3, 0, Integer.MAX_VALUE, "extraHotbarPrioritySlotCount");

    @Override
    public String getName() {
        return "client";
    };
    
};
