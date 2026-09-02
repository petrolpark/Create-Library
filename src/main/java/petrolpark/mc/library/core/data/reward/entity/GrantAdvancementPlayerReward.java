package petrolpark.mc.library.core.data.reward.entity;

import java.util.Optional;

import javax.annotation.ParametersAreNonnullByDefault;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.DisplayInfo;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.storage.loot.LootContext;
import petrolpark.mc.library.core.data.reward.info.INamedRewardInfo;
import petrolpark.mc.library.registry.PetrolparkRewardTypes;
import petrolpark.mc.library.util.AdvancementHelper;
import petrolpark.mc.library.util.Lang.IndentedTooltipBuilder;
import petrolpark.mc.library.util.codec.CodecHelper;

@ParametersAreNonnullByDefault
public record GrantAdvancementPlayerReward(ResourceLocation advancementId, String criterion) implements IPlayerReward {

    public static final MapCodec<GrantAdvancementPlayerReward> CODEC = RecordCodecBuilder.mapCodec(instance ->
        instance.group(
            ResourceLocation.CODEC.fieldOf("advancement").forGetter(GrantAdvancementPlayerReward::advancementId),
            Codec.STRING.fieldOf("criterion").forGetter(GrantAdvancementPlayerReward::criterion)
        ).apply(instance, GrantAdvancementPlayerReward::new)
    );

    @Override
    public boolean rewardPlayer(ServerPlayer player, LootContext context, float multiplier, boolean simulate) {
        final AdvancementHolder advancement = context.getLevel().getServer().getAdvancements().get(advancementId());
        if (advancement == null) return false;
        if (!simulate) player.getAdvancements().award(advancement, criterion());
        return true;
    };

    @Override
    public GrantAdvancementPlayerReward.Info info() {
        return new GrantAdvancementPlayerReward.Info(AdvancementHelper.resolve(advancementId())
            .map(AdvancementHolder::value)
            .flatMap(Advancement::display)
            .<Either<ResourceLocation, DisplayInfo>>map(Either::right)
            .orElseGet(() -> Either.left(advancementId()))
        );
    };

    public record Info(Either<ResourceLocation, DisplayInfo> displayInfo) implements INamedRewardInfo {

        public static final MapCodec<GrantAdvancementPlayerReward.Info> CODEC = CodecHelper.singleFieldMap(Codec.either(ResourceLocation.CODEC, DisplayInfo.CODEC), "advancement", GrantAdvancementPlayerReward.Info::displayInfo, GrantAdvancementPlayerReward.Info::new);
        public static final StreamCodec<RegistryFriendlyByteBuf, GrantAdvancementPlayerReward.Info> STREAM_CODEC = ByteBufCodecs.either(ResourceLocation.STREAM_CODEC, DisplayInfo.STREAM_CODEC).map(GrantAdvancementPlayerReward.Info::new, GrantAdvancementPlayerReward.Info::displayInfo);

        public Info(Either<ResourceLocation, DisplayInfo> displayInfo) {
            // Prefer to store the DisplayInfo as Advancements may not exist on the client
            final Optional<DisplayInfo> resolvedDisplay = displayInfo.left().flatMap(AdvancementHelper::resolve)
                .map(AdvancementHolder::value)
                .flatMap(Advancement::display);

            if (resolvedDisplay.isPresent()) {
                this.displayInfo = Either.right(resolvedDisplay.get());
            } else {
                this.displayInfo = displayInfo;
            };
        };

        @Override
        public void render(GuiGraphics graphics) {
            displayInfo().right().map(DisplayInfo::getIcon).ifPresent(stack -> graphics.renderItem(stack, 0, 0));
        };

        @Override
        public void addToDescription(IndentedTooltipBuilder builder) {
            displayInfo()
                .ifLeft(rl -> builder.add(translate("unknown", rl)))
                .ifRight(info -> builder
                    .add(translateSimple(info.getTitle()))
                    .indent()
                    .add(info.getDescription())
                    .unindent()
                );
        };

        @Override
        public EntityRewardAndInfoType getRewardInfoType() {
            return PetrolparkRewardTypes.ENTITY_GRANT_ADVANCEMENT.get();
        };

    };

    @Override
    public EntityRewardAndInfoType getType() {
        return PetrolparkRewardTypes.ENTITY_GRANT_ADVANCEMENT.get();
    };
    
    
};
