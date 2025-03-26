package com.petrolpark.compat.create;

import com.petrolpark.compat.create.core.tube.BuildTubePacket;
import com.petrolpark.network.PetrolparkMessages;

public class CreateMessages {
  
    public static final void register() {
        PetrolparkMessages.addC2SPacket(BuildTubePacket.class, BuildTubePacket::new);
    };
};
