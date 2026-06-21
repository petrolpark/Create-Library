package petrolpark.mc.library.core.data.loot.numberprovider.team;

import org.jetbrains.annotations.ApiStatus;

import com.mojang.serialization.Codec;
import petrolpark.mc.library.core.data.loot.numberprovider.ContextTeamNumberProvider;
import petrolpark.mc.library.core.data.loot.numberprovider.NumberEstimate;
import petrolpark.mc.library.core.data.loot.numberprovider.entity.EntityNumberProvider;
import petrolpark.mc.library.core.data.loot.numberprovider.itemstack.ItemStackNumberProvider;
import petrolpark.mc.library.core.world.entity.player.team.ITeam;
import petrolpark.mc.library.registry.PetrolparkRegistries;

import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootContextUser;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.NumberProviders;

/**
 * Team-specific version of {@link NumberProvider}.
 * 
 * @see ContextTeamNumberProvider Get a value from a Team in the LootContext
 * @see EntityNumberProvider Entity equivalent
 * @see ItemStackNumberProvider Item Stack equivalent
 */
public interface TeamNumberProvider extends LootContextUser {

    /**
     * Use {@link TeamNumberProvider#CODEC} instead.
     */
    @ApiStatus.Internal
    static final Codec<TeamNumberProvider> TYPED_CODEC = PetrolparkRegistries.LOOT_TEAM_NUMBER_PROVIDER_TYPES
        .byNameCodec()
        .dispatch(TeamNumberProvider::getTeamNumberProviderType, LootTeamNumberProviderType::codec);

    public static final Codec<TeamNumberProvider> CODEC = Codec.lazyInitialized(
        () -> Codec.withAlternative(TYPED_CODEC, NumberProviders.CODEC.xmap(FlatTeamNumberProvider::new, FlatTeamNumberProvider::numberProvider))
    );
    
    public float getFloat(ITeam team, LootContext context);

    /**
     * Get the approximate bounds for the {@link TeamNumberProvider#getFloat(ITeam, LootContext) output} of this {@link TeamNumberProvider} on a best-effort basis.
     * @see NumberEstimate#unknown() 
     */
    public NumberEstimate getEstimate();

    public default float getMaxFloat(ITeam team, LootContext context) {
        return getFloat(team, context);
    };

    public LootTeamNumberProviderType getTeamNumberProviderType();
};
