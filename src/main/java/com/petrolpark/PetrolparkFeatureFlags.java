package com.petrolpark;

import com.petrolpark.util.Lang;

import net.minecraft.client.Minecraft;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.world.flag.FeatureFlag;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.flag.FeatureFlags;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.AddPackFindersEvent;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

@EventBusSubscriber
public enum PetrolparkFeatureFlags {

    EXTENDED_INVENTORY;

    public final FeatureFlag featureFlag;

    PetrolparkFeatureFlags() {
        featureFlag = FeatureFlags.REGISTRY.getFlag(Petrolpark.asResource(Lang.asId(name())));
    };

    public boolean isEnabled() {
        return isEnabled(getEnabledFeatures());
    };

    public boolean isEnabled(FeatureFlagSet flagSet) {
        return flagSet.contains(featureFlag);
    };

    @SuppressWarnings("null")
    public static FeatureFlagSet getEnabledFeatures() {
        return Petrolpark.runForDist(() -> Minecraft.getInstance().getConnection()::enabledFeatures, () -> ServerLifecycleHooks.getCurrentServer().getWorldData()::enabledFeatures);
    };

    public static boolean isEnabled(FeatureFlag flag) {
        return getEnabledFeatures().contains(flag);
    };

    @SubscribeEvent
    public static final void onAddPackFinders(AddPackFindersEvent event) {
        for (PetrolparkFeatureFlags flag : values()) {
            String id = Lang.asId(flag.name());
            event.addPackFinders(
                Petrolpark.asResource("data/petrolpark/datapack/experimental/"+id),
                PackType.SERVER_DATA,
                Lang.translate("dataPack."+id+".name"),
                PackSource.FEATURE,
                false,
                Pack.Position.TOP
            );
        };
    };
};
