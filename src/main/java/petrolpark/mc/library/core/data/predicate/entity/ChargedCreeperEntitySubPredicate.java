package petrolpark.mc.library.core.data.predicate.entity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.advancements.critereon.EntitySubPredicate;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.phys.Vec3;

public record ChargedCreeperEntitySubPredicate(boolean isCharged) implements EntitySubPredicate {

    public static final MapCodec<ChargedCreeperEntitySubPredicate> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        Codec.BOOL.optionalFieldOf("charged", true).forGetter(ChargedCreeperEntitySubPredicate::isCharged)
    ).apply(instance, ChargedCreeperEntitySubPredicate::new));

    @Override
    public MapCodec<ChargedCreeperEntitySubPredicate> codec() {
        return CODEC;
    };

    @Override
    public boolean matches(@Nonnull Entity entity, @Nonnull ServerLevel level, @Nullable Vec3 position) {
        return entity instanceof Creeper creeper ? creeper.isPowered() == isCharged : false;
    };
    
};
