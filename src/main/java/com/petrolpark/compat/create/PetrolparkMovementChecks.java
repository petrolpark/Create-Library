package com.petrolpark.compat.create;

import com.petrolpark.compat.create.core.block.multi.MultiMovementChecks;
import com.simibubi.create.api.contraption.BlockMovementChecks;

public class PetrolparkMovementChecks {

    public static final MultiMovementChecks MULTI_CHECKS = new MultiMovementChecks();
    
    static {
        BlockMovementChecks.registerMovementAllowedCheck(MULTI_CHECKS);
        BlockMovementChecks.registerAttachedCheck(MULTI_CHECKS);
    };

    public static void register() {};
};
