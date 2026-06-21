package petrolpark.mc.library.core.data.loot.numberprovider;

import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import petrolpark.mc.library.core.data.loot.numberprovider.itemstack.ItemStackNumberProvider;
import petrolpark.mc.library.core.data.loot.numberprovider.itemstack.LootItemStackNumberProviderType;
import petrolpark.mc.library.core.data.loot.numberprovider.team.LootTeamNumberProviderType;
import petrolpark.mc.library.core.data.loot.numberprovider.team.TeamNumberProvider;
import petrolpark.mc.library.core.world.entity.player.team.ITeam;
import petrolpark.mc.library.registry.PetrolparkNumberProviderTypes;
import petrolpark.mc.library.util.codec.CodecHelper;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;

/**
 * <p>{@code petrolpark:component}</p>
 * 
 * Get the value of a numeric {@link DataComponentType Data Component} of an Item Stack or {@link ITeam}.
 * 
 * Arguments:
 * <ul>
 * <li> {@code component} - The ID of a {@link DataComponentType} which is an Integer, Float, Long or Double
 * </ul>
 * 
 * @author petrolpark
 */
public record DataComponentNumberProvider(DataComponentType<?> componentType) implements ItemStackNumberProvider, TeamNumberProvider {

    public static final MapCodec<DataComponentNumberProvider> CODEC = CodecHelper.singleFieldMap(DataComponentType.CODEC.validate(DataComponentNumberProvider::validate), "component", DataComponentNumberProvider::componentType, DataComponentNumberProvider::new);

    @SuppressWarnings("unchecked")
    public static final DataResult<DataComponentType<?>> validate(DataComponentType<?> componentType) {
        try {
            return DataResult.success((DataComponentType<Integer>)componentType);
        } catch (ClassCastException eint) {
            try {
                return DataResult.success((DataComponentType<Float>)componentType);
            } catch (ClassCastException efloat) {
                try {
                    return DataResult.success((DataComponentType<Long>)componentType);
                } catch (ClassCastException elong) {
                    try {
                        return DataResult.success((DataComponentType<Double>)componentType);
                    } catch (ClassCastException edouble) {
                        return DataResult.error(() -> "Not a numeric Data Component");
                    }
                }
            }
        }
    };

    @Override
    @SuppressWarnings("null")
    public float getFloat(ItemStack stack, LootContext lootContext) {
        try {
            return (float)stack.get(componentType());
        } catch (ClassCastException e) {
            return 0f;
        }
    };

    @Override
    @SuppressWarnings("null")
    public float getFloat(ITeam team, LootContext context) {
        try {
            return (float)team.get(componentType());
        } catch (ClassCastException e) {
            return 0f;
        }
    };

    @Override
    public NumberEstimate getEstimate() {
        return NumberEstimate.UNKNOWN;
    };

    @Override
    public LootItemStackNumberProviderType getItemStackNumberProviderType() {
        return PetrolparkNumberProviderTypes.ITEM_DATA_COMPONENT.get();
    };
    
    @Override
    public LootTeamNumberProviderType getTeamNumberProviderType() {
        return PetrolparkNumberProviderTypes.TEAM_DATA_COMPONENT.get();
    };
    
    
};
