package petrolpark.mc.library.core.data.predicate.entity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.mojang.serialization.MapCodec;
import petrolpark.mc.library.util.codec.CodecHelper;

import net.minecraft.advancements.critereon.EntitySubPredicate;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.horse.Horse;
import net.minecraft.world.entity.animal.horse.Markings;
import net.minecraft.world.phys.Vec3;

public record HorseMarkingsEntitySubPredicate(Markings markings) implements EntitySubPredicate {

    public static final MapCodec<HorseMarkingsEntitySubPredicate> CODEC = CodecHelper.singleFieldMap(CodecHelper.HORSE_MARKINGS_CODEC, "markings", HorseMarkingsEntitySubPredicate::markings, HorseMarkingsEntitySubPredicate::new);

    @Override
    public MapCodec<HorseMarkingsEntitySubPredicate> codec() {
        return CODEC;
    };

    @Override
    public boolean matches(@Nonnull Entity entity, @Nonnull ServerLevel level, @Nullable Vec3 position) {
        return entity instanceof Horse horse ? horse.getMarkings() == markings() : false;
    };
    
};
