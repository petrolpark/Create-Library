package petrolpark.mc.library.mixin.compat.jade;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import petrolpark.mc.library.Petrolpark;
import petrolpark.mc.library.config.PetrolparkConfigs;
import petrolpark.mc.library.registry.PetrolparkAttributes;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import snownee.jade.addon.vanilla.HorseStatsProvider;
import snownee.jade.api.EntityAccessor;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

@Mixin(HorseStatsProvider.class)
public class HorseStatsProviderMixin {

    @Shadow
    private static Component switchText(String key, boolean showMax, double value, double max) {
        throw new AssertionError();
    };
    
    @Inject(
        method = "appendTooltip",
        at = @At("TAIL")
    )
    public void petrolpark$addHorseMillStressCapacityTooltip(ITooltip tooltip, EntityAccessor accessor, IPluginConfig config, CallbackInfo ci) {
        if (accessor.getEntity() instanceof AbstractHorse horse && horse.getAttributes().hasAttribute(PetrolparkAttributes.HORSE_MILL_STRESS_CAPACITY)) {
            tooltip.add(switchText("config.jade.plugin_" + Petrolpark.MOD_ID + ".horse_mill_stress_capacity", accessor.showDetails(), horse.getAttributeBaseValue(PetrolparkAttributes.HORSE_MILL_STRESS_CAPACITY), PetrolparkConfigs.common().createHorseMillStressCapacityAttributeMax.getF()));
        };
    };
};
