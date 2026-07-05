package petrolpark.mc.library.compat.create.shared.registry;

import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import com.simibubi.create.content.contraptions.render.ContraptionEntityRenderer;
import com.simibubi.create.content.contraptions.render.ContraptionVisual;
import com.tterrag.registrate.util.entry.EntityEntry;
import com.tterrag.registrate.util.nullness.NonNullConsumer;
import com.tterrag.registrate.util.nullness.NonNullFunction;
import com.tterrag.registrate.util.nullness.NonNullSupplier;

import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityType.EntityFactory;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.neoforge.common.Tags.EntityTypes;
import petrolpark.mc.library.compat.create.PetrolparkCreate;
import petrolpark.mc.library.compat.create.PetrolparkCreateRegistrate;
import petrolpark.mc.library.compat.create.shared.content.kinetics.horseMill.HarnessEntity;
import petrolpark.mc.library.compat.create.shared.content.kinetics.horseMill.HorseMillContraptionEntity;
import petrolpark.mc.library.core.registrate.builder.shared.SharedCreateEntityBuilder;
import petrolpark.mc.library.shared.SharedFeatureFlag;

public class SharedCreateEntityTypes {

    public static final EntityEntry<HorseMillContraptionEntity> HORSE_MILL_CONTRAPTION = register(SharedFeatureFlag.HORSE_MILL, "horse_mill_contraption", HorseMillContraptionEntity::new, () -> ContraptionEntityRenderer::new, MobCategory.MISC, 20, 40, false, true, AbstractContraptionEntity::build)
        .visual(() -> ContraptionVisual::new)
        .tag(EntityTypes.TELEPORTING_NOT_SUPPORTED)
        .register();
    
    public static final EntityEntry<HarnessEntity> HARNESS = SharedCreateEntityTypes.<HarnessEntity>register(SharedFeatureFlag.HORSE_MILL, "harness", HarnessEntity::new, () -> HarnessEntity.Renderer::new, MobCategory.MISC, 5, Integer.MAX_VALUE, false, true, HarnessEntity::build)
        .register();

    private static <T extends Entity> SharedCreateEntityBuilder<T, PetrolparkCreateRegistrate> register(SharedFeatureFlag flag, String name, EntityFactory<T> factory, NonNullSupplier<NonNullFunction<EntityRendererProvider.Context, EntityRenderer<? super T>>> renderer, MobCategory group, int range, int updateFrequency, boolean sendVelocity, boolean immuneToFire, NonNullConsumer<EntityType.Builder<T>> propertyBuilder) {
		return (SharedCreateEntityBuilder<T, PetrolparkCreateRegistrate>) PetrolparkCreate.REGISTRATE.sharedCreateEntity(flag, name, factory, group)
            .properties(b -> b.setTrackingRange(range)
                .setUpdateInterval(updateFrequency)
                .setShouldReceiveVelocityUpdates(sendVelocity))
            .properties(propertyBuilder)
            .properties(b -> {
                if (immuneToFire) b.fireImmune();
            })
            .renderer(renderer);
	};

    public static final void register() {};
};
