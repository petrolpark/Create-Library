package com.petrolpark.compat.jade;

import org.jetbrains.annotations.Nullable;

import com.petrolpark.compat.ISharedFeature;
import com.petrolpark.compat.Mods;
import com.petrolpark.compat.SharedFeatures;
import com.petrolpark.util.Lang;

import net.minecraft.world.item.ItemStack;
import snownee.jade.api.callback.JadeItemModNameCallback;

public class SharedFeatureItemModNameCallback implements JadeItemModNameCallback {

    @Override
    public @Nullable String gatherItemModName(ItemStack stack) {
        if (stack.getItem() instanceof ISharedFeature sharedFeature) {
            SharedFeatures feature = sharedFeature.getSharedFeature();
            if (feature.enabled()) return Lang.shortList(feature.streamUsers().map(Mods::getName).toArray(i -> new String[i]));
        };
        return null;
    };
    
};
