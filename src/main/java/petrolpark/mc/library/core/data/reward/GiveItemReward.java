package petrolpark.mc.library.core.data.reward;

import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import javax.annotation.ParametersAreNonnullByDefault;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import petrolpark.mc.library.registry.PetrolparkRewardTypes;
import petrolpark.mc.library.util.Lang.IndentedTooltipBuilder;

/**
 * <p>{@code petrolpark:give_item}</p>
 * 
 * Give a single Item Stack to an Entity by putting it into its Inventory or dropping it.
 * 
 * Arguments:
 * <ul>
 * <li> {@code item} - {@link ItemStack} to give to the recipient
 * </ul>
 * 
 * @author petrolpark
 * @apiNote The {@link AbstractGiveItemsReward#itemFunctions() late LootItemFunctions} are not carried to the client at all
 */
@ParametersAreNonnullByDefault
public class GiveItemReward extends AbstractGiveItemsReward implements ISimpleReward {

    public static final MapCodec<GiveItemReward> CODEC = RecordCodecBuilder.mapCodec(instance ->
        instance.group(
            ItemStack.CODEC.fieldOf("item").forGetter(GiveItemReward::stack)
        ).and(lateItemFunctionsField(instance).t1())
        .apply(instance, GiveItemReward::new)
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, GiveItemReward> STREAM_CODEC = StreamCodec.composite(ItemStack.STREAM_CODEC, GiveItemReward::stack, GiveItemReward::new);

    private final ItemStack stack;

    public GiveItemReward(ItemStack stack) {
        this(stack, Collections.emptyList());
    };

    public GiveItemReward(ItemStack stack, List<LootItemFunction> functions) {
        super(functions);
        this.stack = stack;
    };

    public ItemStack stack() {
        return stack;
    };

    @Override
    public Stream<ItemStack> streamStacks(LootContext context) {
        return Stream.of(stack());
    };

    @Override
    public void render(GuiGraphics graphics) {
        graphics.renderItem(stack, 0, 0);
    };

    @Override
    public void addToDescription(IndentedTooltipBuilder builder) {
        builder.add(translateSimple(stack.getDisplayName()));
    };

    @Override
    public RewardAndInfoType getType() {
        return PetrolparkRewardTypes.GIVE_ITEM.get();
    };
    
};
