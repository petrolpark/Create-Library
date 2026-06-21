package petrolpark.mc.library.core.data.condition;

import javax.annotation.Nonnull;

import com.mojang.serialization.MapCodec;
import petrolpark.mc.library.registry.PetrolparkDataLoadingConditions;
import petrolpark.mc.library.shared.SharedFeatureFlag;
import petrolpark.mc.library.util.codec.CodecHelper;

import net.neoforged.neoforge.common.conditions.ICondition;

public record SharedFeatureEnabledCondition(SharedFeatureFlag featureFlag) implements ICondition {

    public static final MapCodec<SharedFeatureEnabledCondition> CODEC = CodecHelper.singleFieldMap(SharedFeatureFlag.CODEC, "feature", SharedFeatureEnabledCondition::featureFlag, SharedFeatureEnabledCondition::new);

    @Override
    public boolean test(@Nonnull IContext context) {
        return featureFlag.enabled();
    };

    @Override
    public MapCodec<? extends ICondition> codec() {
        return PetrolparkDataLoadingConditions.FEATURE_ENABLED.get();
    };
    
};
