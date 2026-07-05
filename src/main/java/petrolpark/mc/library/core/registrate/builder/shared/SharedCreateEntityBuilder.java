package petrolpark.mc.library.core.registrate.builder.shared;

import com.simibubi.create.foundation.data.CreateEntityBuilder;
import com.tterrag.registrate.builders.BuilderCallback;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType.EntityFactory;
import net.minecraft.world.entity.MobCategory;
import petrolpark.mc.library.compat.create.core.registrate.AbstractPetrolparkCreateRegistrate;
import petrolpark.mc.library.shared.SharedFeatureFlag;

public class SharedCreateEntityBuilder<T extends Entity, P> extends CreateEntityBuilder<T, P> {

    public final SharedFeatureFlag featureFlag;

    public static <T extends Entity, P> SharedCreateEntityBuilder<T, P> create(AbstractPetrolparkCreateRegistrate<?> owner, P parent, SharedFeatureFlag featureFlag, String name, BuilderCallback callback, EntityFactory<T> factory, MobCategory classification) {
		final SharedCreateEntityBuilder<T, P> builder = new SharedCreateEntityBuilder<>(owner, parent, featureFlag, name, callback, factory, classification);
        builder.asOptional();
        return builder;
	};

    protected SharedCreateEntityBuilder(AbstractPetrolparkCreateRegistrate<?> owner, P parent, SharedFeatureFlag featureFlag, String name, BuilderCallback callback, EntityFactory<T> factory, MobCategory classification) {
        super(owner, parent, name, callback, factory, classification);
        this.featureFlag = featureFlag;
    };

    @Override
    protected void registerRenderer() {
        if (featureFlag.enabled()) super.registerRenderer();
    };

    @Override
    protected void registerVisualizer() {
        if (featureFlag.enabled()) super.registerVisualizer();
    };
    
};
