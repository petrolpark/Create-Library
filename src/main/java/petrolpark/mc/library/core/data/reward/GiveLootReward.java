package petrolpark.mc.library.core.data.reward;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import javax.annotation.ParametersAreNonnullByDefault;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import petrolpark.mc.library.Petrolpark;
import petrolpark.mc.library.core.data.loot.ILootTableAccessor;
import petrolpark.mc.library.core.data.reward.info.INamedRewardInfo;
import petrolpark.mc.library.core.data.reward.info.IRewardInfo;
import petrolpark.mc.library.registry.PetrolparkRewardTypes;
import petrolpark.mc.library.util.ItemHelper;
import petrolpark.mc.library.util.Lang;
import petrolpark.mc.library.util.Lang.IndentedTooltipBuilder;
import petrolpark.mc.library.util.RenderHelper;

/**
 * <p>{@code petrolpark:give_loot}</p>
 * 
 * Give randomly-generated Item Stacks from a Loot Table to an Entity by putting them into its Inventory or dropping them.
 * 
 * Arguments:
 * <ul>
 * <li> {@code loot_table} - ID of a {@link LootTable} or an inline definition
 * </ul>
 * 
 * @author petrolpark
 */
@ParametersAreNonnullByDefault
public class GiveLootReward extends AbstractGiveItemsReward implements ILootTableAccessor {

    public static final MapCodec<GiveLootReward> CODEC = RecordCodecBuilder.mapCodec(instance -> 
        ILootTableAccessor.lootTableField(instance)
        .and(lateItemFunctionsField(instance).t1())
        .apply(instance, GiveLootReward::new)
    );

    protected final Either<ResourceKey<LootTable>, LootTable> lootTable;

    protected List<ItemStack> possibleStacks;

    public GiveLootReward(Either<ResourceKey<LootTable>, LootTable> lootTable, List<LootItemFunction> functions) {
        super(functions);
        this.lootTable = lootTable;
    };

    @Override
    public Either<ResourceKey<LootTable>, LootTable> lootTable() {
        return lootTable;
    };

    @Override
    public Stream<ItemStack> streamStacks(LootContext context) {
        final List<ItemStack> stacks = new ArrayList<>();
        getLootTable(context).getRandomItems(context, stacks::add);
        return stacks.stream();
    };

    @Override
    public IRewardInfo info() {
        if (possibleStacks == null) possibleStacks = lootTable().map($ -> Collections.emptyList(), table -> ItemHelper.streamPossibleItems(table).toList());
        //TODO trim stream to resonable size
        return new GiveLootReward.Info(possibleStacks, lootTable().left().map(ResourceKey::location));
    };

    public record Info(List<ItemStack> possibleStacks, Optional<ResourceLocation> lootTableLocation) implements INamedRewardInfo {

        public static final ResourceLocation DIE_TEXTURE = Petrolpark.asResource("item/gui/die");

        public static final MapCodec<GiveLootReward.Info> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                ItemStack.CODEC.listOf().fieldOf("possible_stacks").forGetter(GiveLootReward.Info::possibleStacks),
                ResourceLocation.CODEC.optionalFieldOf("loot_table").forGetter(GiveLootReward.Info::lootTableLocation)
            ).apply(instance, GiveLootReward.Info::new)
        );

        public static final StreamCodec<RegistryFriendlyByteBuf, GiveLootReward.Info> STREAM_CODEC = StreamCodec.composite(
            ItemStack.LIST_STREAM_CODEC, GiveLootReward.Info::possibleStacks,
            ByteBufCodecs.optional(ResourceLocation.STREAM_CODEC), GiveLootReward.Info::lootTableLocation,
            GiveLootReward.Info::new
        );

        @Override
        public void render(GuiGraphics graphics) {
            if (!possibleStacks.isEmpty()) {
                graphics.renderItem(RenderHelper.cycle(possibleStacks), 0, 0);
                graphics.pose().translate(0f, 8f, 0f);
                graphics.pose().scale(0.5f, 0.5f, 0.5f);
            };
            graphics.blit(0, 0, 0, 16, 16, Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(DIE_TEXTURE));
            graphics.pose().popPose();
        };

        @Override
        public void addToDescription(IndentedTooltipBuilder builder) {
            builder.add(lootTableLocation.map(id -> translateSimple(Lang.loot(id))).orElse(translate("unknown_table")));
        };

        @Override
        public RewardAndInfoType getRewardInfoType() {
            return PetrolparkRewardTypes.GIVE_LOOT.get();
        };

    };

    @Override
    public RewardAndInfoType getType() {
        return PetrolparkRewardTypes.GIVE_LOOT.get();
    };
    
};
