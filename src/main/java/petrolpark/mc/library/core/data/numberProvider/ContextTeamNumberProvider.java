package petrolpark.mc.library.core.data.numberProvider;

import javax.annotation.ParametersAreNonnullByDefault;

import com.mojang.serialization.MapCodec;

import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.providers.number.LootNumberProviderType;
import petrolpark.mc.library.core.data.numberProvider.team.TeamNumberProvider;
import petrolpark.mc.library.core.world.entity.player.team.ITeam;
import petrolpark.mc.library.registry.PetrolparkLootContextParams;
import petrolpark.mc.library.registry.PetrolparkNumberProviderTypes;
import petrolpark.mc.library.util.codec.CodecHelper;

/**
 * <p>{@code petrolpark:team_property}</p>
 * 
 * Get a {@link TeamNumberProvider} value of the {@link PetrolparkLootContextParams#TEAM Team provided} in the {@link LootContext}.
 * 
 * Arguments:
 * <ul>
 * <li> {@code value} - A {@link TeamNumberProvider} to call on the {@link ITeam} 
 * </ul>
 * 
 * @author petrolpark
 */
@ParametersAreNonnullByDefault
public record ContextTeamNumberProvider(TeamNumberProvider value) implements IEstimableNumberProvider {

    public static final MapCodec<ContextTeamNumberProvider> CODEC = CodecHelper.singleFieldMap(TeamNumberProvider.CODEC, "value", ContextTeamNumberProvider::value, ContextTeamNumberProvider::new);
    
    @Override
    public float getFloat(LootContext context) {
        final ITeam team = context.getParam(PetrolparkLootContextParams.TEAM);
        if (team != null) return value.getFloat(team, context);
        return 0f;
    };

    @Override
    public int getInt(LootContext context) {
        final ITeam team = context.getParam(PetrolparkLootContextParams.TEAM);
        if (team != null) return value.getInt(team, context);
        return 0;
    };

    @Override
    public NumberEstimate getEstimate() {
        return value().getEstimate();
    };

    @Override
    public float getMaxFloat(LootContext context) {
        ITeam team = context.getParam(PetrolparkLootContextParams.TEAM);
        if (team != null) return value.getMaxFloat(team, context);
        return 0f;
    };

    @Override
    public LootNumberProviderType getType() {
        return PetrolparkNumberProviderTypes.CONTEXT_TEAM.get();
    };

    @Override
    public void validate(ValidationContext context) {
        IEstimableNumberProvider.super.validate(context);
        value().validate(context.forChild(".team_number_provider"));
    };
    
};
