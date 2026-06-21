package petrolpark.mc.library.core.data.loot.numberprovider;

import java.util.Collections;
import java.util.Set;

import javax.annotation.Nonnull;

import com.mojang.serialization.MapCodec;
import petrolpark.mc.library.core.data.loot.numberprovider.itemstack.ItemStackNumberProvider;
import petrolpark.mc.library.registry.PetrolparkNumberProviderTypes;
import petrolpark.mc.library.util.codec.CodecHelper;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.providers.number.LootNumberProviderType;

/**
 * <p>{@code petrolpark:tool_property}</p>
 * 
 * Get a {@link ItemStackNumberProvider} value of the {@link LootContextParams#TOOL Item Stack tool provided} in the {@link LootContext}.
 * 
 * Arguments:
 * <ul>
 * <li> {@code value} - An {@link ItemStackNumberProvider} to call on the tool Item Stack
 * </ul>
 * 
 * @author petrolpark
 */
public record ContextToolNumberProvider(ItemStackNumberProvider value) implements IEstimableNumberProvider {

    public static final MapCodec<ContextToolNumberProvider> CODEC = CodecHelper.singleFieldMap(ItemStackNumberProvider.CODEC, "value", ContextToolNumberProvider::value, ContextToolNumberProvider::new);

    @Override
    public float getFloat(@Nonnull LootContext lootContext) {
        ItemStack tool = lootContext.getParamOrNull(LootContextParams.TOOL);
        if (tool != null) return value.getFloat(tool, lootContext);
        return 0f;
    };

    @Override
    public NumberEstimate getEstimate() {
        return value().getEstimate();
    };

    @Override
    public float getMaxFloat(LootContext context) {
        ItemStack tool = context.getParamOrNull(LootContextParams.TOOL);
        if (tool != null) return value.getMaxFloat(tool, context);
        return 0f;
    };

    @Override
    public Set<LootContextParam<?>> getReferencedContextParams() {
        return Collections.singleton(LootContextParams.TOOL);
    };

    @Override
    public LootNumberProviderType getType() {
        return PetrolparkNumberProviderTypes.CONTEXT_TOOL.get();
    };
    
};
