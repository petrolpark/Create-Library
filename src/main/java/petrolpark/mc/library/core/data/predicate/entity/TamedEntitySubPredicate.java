package petrolpark.mc.library.core.data.predicate.entity;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import com.mojang.serialization.MapCodec;

import net.minecraft.advancements.critereon.EntitySubPredicate;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.phys.Vec3;
import petrolpark.mc.library.registry.PetrolparkDataSubPredicates;

/**
 * <p>{@code petrolpark:tamed}</p>
 * 
 * Checks if the entity is tamed. No arguments.
 * 
 * @author petrolpark
 */
@ParametersAreNonnullByDefault
public final class TamedEntitySubPredicate implements EntitySubPredicate {

    @Override
    public MapCodec<? extends EntitySubPredicate> codec() {
        return PetrolparkDataSubPredicates.ENTITY_TAMED.get();
    };

    @Override
    public boolean matches(Entity entity, ServerLevel level, @Nullable Vec3 position) {
        return entity instanceof TamableAnimal tamable ? tamable.isTame() : entity instanceof OwnableEntity ownable ? ownable.getOwnerUUID() != null : false;
    };
    
};