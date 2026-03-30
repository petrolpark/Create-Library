package com.petrolpark.core.data.reward.entity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.petrolpark.Petrolpark;
import com.petrolpark.PetrolparkRewardTypes;
import com.petrolpark.core.data.loot.ILootTableAccessor;
import com.petrolpark.util.ItemHelper;
import com.petrolpark.util.Lang;
import com.petrolpark.util.Lang.IndentedTooltipBuilder;
import com.petrolpark.util.RenderHelper;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;

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
public class GiveLootEntityReward extends AbstractGiveItemsEntityReward implements ILootTableAccessor {

    public static final ResourceLocation DIE_TEXTURE = Petrolpark.asResource("item/die");

    public static final MapCodec<GiveLootEntityReward> CODEC = RecordCodecBuilder.mapCodec(instance -> 
        ILootTableAccessor.lootTableField(instance)
        .and(lateItemFunctionsField(instance).t1())
        .apply(instance, GiveLootEntityReward::new)
    );

    protected final Either<ResourceKey<LootTable>, LootTable> lootTable;

    protected List<ItemStack> possibleStacks;

    public GiveLootEntityReward(Either<ResourceKey<LootTable>, LootTable> lootTable, List<LootItemFunction> functions) {
        super(functions);
        this.lootTable = lootTable;
    };

    @Override
    public Either<ResourceKey<LootTable>, LootTable> lootTable() {
        return lootTable;
    };

    @Override
    public Stream<ItemStack> streamStacks(Entity recipient, LootContext context) {
        List<ItemStack> stacks = new ArrayList<>();
        getLootTable(context).getRandomItems(context, stacks::add);
        return stacks.stream();
    };

    @Override
    public void render(GuiGraphics graphics) {
        if (possibleStacks == null) possibleStacks = lootTable().map($ -> Collections.emptyList(), table -> ItemHelper.streamPossibleItems(table).toList());
        graphics.pose().pushPose();
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
        builder.add(lootTable.map(
            key -> translateSimple(Lang.loot(key.location())),
            table -> translate("unknown_table")
        ));
    };

    @Override
    public EntityRewardType getType() {
        return PetrolparkRewardTypes.GIVE_LOOT.get();
    };
    
};
