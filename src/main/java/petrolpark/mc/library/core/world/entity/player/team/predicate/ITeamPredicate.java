package petrolpark.mc.library.core.world.entity.player.team.predicate;

import javax.annotation.ParametersAreNonnullByDefault;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;

import net.minecraft.core.Holder;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootContextUser;
import petrolpark.mc.library.core.world.entity.player.team.ITeam;
import petrolpark.mc.library.registry.PetrolparkRegistries;

@ParametersAreNonnullByDefault
public interface ITeamPredicate extends LootContextUser {

    /**
     * Use {@link ITeamPredicate#DIRECT_CODEC} instead.
     */
    static final Codec<ITeamPredicate> TYPED_CODEC = PetrolparkRegistries.TEAM_PREDICATE_TYPES
        .byNameCodec()
        .dispatch("team_predicate_type", ITeamPredicate::getTeamPredicateType, ITeamPredicate.Type::codec);

    public static final Codec<ITeamPredicate> DIRECT_CODEC = Codec.lazyInitialized(() -> TYPED_CODEC);

    public static final Codec<Holder<ITeamPredicate>> CODEC = RegistryFileCodec.create(PetrolparkRegistries.Keys.TEAM_PREDICATE, DIRECT_CODEC);
    
    public boolean test(LootContext context, ITeam team);

    public ITeamPredicate.Type getTeamPredicateType();

    public record Type(MapCodec<? extends ITeamPredicate> codec) {};
};
