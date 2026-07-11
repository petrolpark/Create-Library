package petrolpark.mc.library.core.data.reward.entity;

import javax.annotation.ParametersAreNonnullByDefault;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.NumberProviders;
import petrolpark.mc.library.Petrolpark;
import petrolpark.mc.library.core.data.numberProvider.NumberEstimate;
import petrolpark.mc.library.core.data.reward.info.INamedRewardInfo;
import petrolpark.mc.library.registry.PetrolparkRewardTypes;
import petrolpark.mc.library.util.Lang.IndentedTooltipBuilder;
import petrolpark.mc.library.util.codec.CodecHelper;

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
@ParametersAreNonnullByDefault
public record GrantExperiencePlayerReward(NumberProvider amount) implements IPlayerReward {

    public static final ResourceLocation EXPERIENCE_ORBS_TEXTURE = Petrolpark.asResource("items/gui/experience_orbs");

    public static final MapCodec<GrantExperiencePlayerReward> CODEC = CodecHelper.singleFieldMap(NumberProviders.CODEC, "amount", GrantExperiencePlayerReward::amount, GrantExperiencePlayerReward::new);

    @Override
    public boolean rewardPlayer(ServerPlayer player, LootContext context, float multiplier, boolean simulate) {
        if (!simulate) player.giveExperiencePoints((int)(amount.getFloat(context) * multiplier));
        return true;
    };

    @Override
    public GrantExperiencePlayerReward.Info info() {
        return new GrantExperiencePlayerReward.Info(NumberEstimate.get(amount()));
    };

    public record Info(NumberEstimate amount) implements INamedRewardInfo {

        public static final MapCodec<GrantExperiencePlayerReward.Info> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                NumberEstimate.fieldCodec("amount").forGetter(GrantExperiencePlayerReward.Info::amount)
            ).apply(instance, GrantExperiencePlayerReward.Info::new)
        );

        public static final StreamCodec<ByteBuf, GrantExperiencePlayerReward.Info> STREAM_CODEC = StreamCodec.composite(NumberEstimate.STREAM_CODEC, GrantExperiencePlayerReward.Info::amount, GrantExperiencePlayerReward.Info::new);

        @Override
        public void render(GuiGraphics graphics) {
            graphics.blit(0, 0, 0, 16, 16, Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(EXPERIENCE_ORBS_TEXTURE));
        };

        @Override
        public void addToDescription(IndentedTooltipBuilder builder) {
            if (amount().unknown() || amount().equals(NumberEstimate.POSITIVE)) builder.add(translate("unknown_amount"));
            else builder.add(translateSimple(amount().getIntComponent()));
        };

        @Override
        public EntityRewardAndInfoType getRewardInfoType() {
            return PetrolparkRewardTypes.GRANT_EXPERIENCE.get();
        };
    };

    @Override
    public EntityRewardAndInfoType getType() {
        return PetrolparkRewardTypes.GRANT_EXPERIENCE.get();
    };
    
};
