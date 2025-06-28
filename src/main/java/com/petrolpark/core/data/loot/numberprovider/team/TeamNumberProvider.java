package com.petrolpark.core.data.loot.numberprovider.team;

import com.mojang.serialization.Codec;
import com.petrolpark.PetrolparkRegistries;
import com.petrolpark.core.data.loot.numberprovider.NumberEstimate;
import com.petrolpark.core.team.ITeam;

import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootContextUser;

public interface TeamNumberProvider extends LootContextUser {

    /**
     * Use {@link TeamNumberProvider#CODEC} instead.
     */
    static final Codec<TeamNumberProvider> TYPED_CODEC = PetrolparkRegistries.LOOT_TEAM_NUMBER_PROVIDER_TYPES
        .byNameCodec()
        .dispatch(TeamNumberProvider::getType, LootTeamNumberProviderType::codec);

    public static final Codec<TeamNumberProvider> CODEC = Codec.lazyInitialized(
        () -> Codec.withAlternative(TYPED_CODEC, Codec.unit(MembersTeamNumberProvider::new)) //TODO inline
    );
    
    public float getFloat(ITeam team, LootContext context);

    public NumberEstimate getEstimate();

    public default float getMaxFloat(ITeam team, LootContext context) {
        return getFloat(team, context);
    };

    public LootTeamNumberProviderType getType();
};
