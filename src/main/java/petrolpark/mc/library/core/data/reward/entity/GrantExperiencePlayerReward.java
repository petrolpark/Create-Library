package petrolpark.mc.library.core.data.reward.entity;

import com.mojang.serialization.MapCodec;
import petrolpark.mc.library.Petrolpark;
import petrolpark.mc.library.core.data.loot.numberprovider.NumberEstimate;
import petrolpark.mc.library.registry.PetrolparkRewardTypes;
import petrolpark.mc.library.util.Lang.IndentedTooltipBuilder;
import petrolpark.mc.library.util.codec.CodecHelper;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.NumberProviders;

/**
 * <p>{@code petrolpark:grant_experience}</p>
 * 
 * Give a Player some XP, or do nothing if they are a non-Player Entity.
 * 
 * Arguments:
 * <ul>
 * <li> {@code amount} - A {@link NumberProvider} for the amount of experience 
 * </ul>
 * 
 * @author petrolpark
 */
public record GrantExperiencePlayerReward(NumberProvider amount) implements IPlayerReward {

    public static final ResourceLocation EXPERIENCE_ORBS_TEXTURE = Petrolpark.asResource("items/gui/experience_orbs");

    public static final MapCodec<GrantExperiencePlayerReward> CODEC = CodecHelper.singleFieldMap(NumberProviders.CODEC, "amount", GrantExperiencePlayerReward::amount, GrantExperiencePlayerReward::new);

    @Override
    public void rewardPlayer(Player player, LootContext context, float multiplier) {
        player.giveExperiencePoints((int)(amount.getFloat(context) * multiplier));
    };

    @Override
    public void render(GuiGraphics graphics) {
        graphics.blit(0, 0, 0, 16, 16, Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(EXPERIENCE_ORBS_TEXTURE));
    };

    @Override
    public void addToDescription(IndentedTooltipBuilder builder) {
        NumberEstimate amount = NumberEstimate.get(amount());
        if (amount.unknown()) builder.add(translate("unknown_amount"));
        else builder.add(translateSimple(amount.getIntComponent()));
    };

    @Override
    public EntityRewardType getType() {
        return PetrolparkRewardTypes.GRANT_EXPERIENCE.get();
    };
    
};
