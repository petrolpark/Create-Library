package petrolpark.mc.library.core.data.predicate.entity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.mojang.serialization.MapCodec;
import petrolpark.mc.library.util.codec.CodecHelper;

import net.minecraft.advancements.critereon.EntitySubPredicate;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.phys.Vec3;

public record VillagerProfessionEntitySubPredicate(Holder<VillagerProfession> profession) implements EntitySubPredicate {

    public static final MapCodec<VillagerProfessionEntitySubPredicate> CODEC = CodecHelper.singleFieldMap(BuiltInRegistries.VILLAGER_PROFESSION.holderByNameCodec(), "profession", VillagerProfessionEntitySubPredicate::profession, VillagerProfessionEntitySubPredicate::new);

    @Override
    public MapCodec<VillagerProfessionEntitySubPredicate> codec() {
        return CODEC;
    };

    @Override
    public boolean matches(@Nonnull Entity entity, @Nonnull ServerLevel level, @Nullable Vec3 position) {
        return entity instanceof Villager villager && villager.getVillagerData().getProfession().equals(profession.value());
    };
    
};
