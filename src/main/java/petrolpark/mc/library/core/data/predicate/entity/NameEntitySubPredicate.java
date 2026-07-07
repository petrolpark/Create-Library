package petrolpark.mc.library.core.data.predicate.entity;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.advancements.critereon.EntitySubPredicate;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import petrolpark.mc.library.registry.PetrolparkDataSubPredicates;

/**
 * <p>{@code petrolpark:name}</p>
 * 
 * Checks whether an Entity's custom name matches a given regex (or indeed, if they have a custom name).
 * 
 * Arguments:
 * <ul>
 * <li> {@code regex} - RegEx to match. Defaults to {@code "*"} (always matching)
 * </ul>
 * 
 * @author petrolpark
 */
@ParametersAreNonnullByDefault
public record NameEntitySubPredicate(String regex) implements EntitySubPredicate {

    public static final MapCodec<NameEntitySubPredicate> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        Codec.STRING.optionalFieldOf("regex", "*").forGetter(NameEntitySubPredicate::regex)
    ).apply(instance, NameEntitySubPredicate::new));

    @Override
    public MapCodec<? extends EntitySubPredicate> codec() {
        return PetrolparkDataSubPredicates.ENTITY_NAME.get();
    };

    @Override
    public boolean matches(Entity entity, ServerLevel level, @Nullable Vec3 position) {
        final Component customName = entity.getCustomName();
        return customName != null && customName.getString().matches(regex());
    };
    
};
