package petrolpark.mc.library.core.data.loot.numberprovider.team;

import petrolpark.mc.library.core.data.loot.numberprovider.NumberEstimate;
import petrolpark.mc.library.core.world.entity.player.team.ITeam;
import petrolpark.mc.library.registry.PetrolparkNumberProviderTypes;

import net.minecraft.world.level.storage.loot.LootContext;

/**
 * <p>{@code petrolpark:member_count}</p>
 * 
 * {@link ITeam#memberCount() Get the number of members} of the {@link ITeam}. No arguments.
 * 
 * @author petrolpark
 */
public class MemberCountTeamNumberProvider implements TeamNumberProvider {

    @Override
    public float getFloat(ITeam team, LootContext context) {
        return team.memberCount();
    };

    @Override
    public NumberEstimate getEstimate() {
        return NumberEstimate.UNKNOWN;
    };

    @Override
    public LootTeamNumberProviderType getTeamNumberProviderType() {
        return PetrolparkNumberProviderTypes.MEMBER_COUNT.get();
    };
    
};
