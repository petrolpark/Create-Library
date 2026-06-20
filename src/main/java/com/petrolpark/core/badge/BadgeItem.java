package com.petrolpark.core.badge;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;

import javax.annotation.Nonnull;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.petrolpark.registry.PetrolparkDataComponentTypes;
import com.petrolpark.util.ItemHelper;

import io.netty.buffer.ByteBuf;
import net.minecraft.ChatFormatting;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

public class BadgeItem extends Item {

    protected static final DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
    protected static final Style PRIMARY = Style.EMPTY.withColor(0xC9974C);
    protected static final Style HIGHLIGHT = Style.EMPTY.withColor(0xF1DD79);

    public final Supplier<Badge> badge;

    public BadgeItem(Properties properties, Supplier<Badge> badge) {
        super(properties);
        this.badge = badge;
    };

    public static ItemStack of(Player player, Badge badge, Date date) {
        ItemStack stack = new ItemStack(badge.asItem());
        stack.set(PetrolparkDataComponentTypes.BADGE_AWARD, new BadgeAward(player.getUUID(), date.getTime()));
        return stack;
    };

    @Override
    public Component getName(@Nonnull ItemStack stack) {
        return badge.get().getName();
    };

    @Override
    public void appendHoverText(@Nonnull ItemStack stack, @Nonnull Item.TooltipContext context, @Nonnull List<Component> tooltipComponents, @Nonnull TooltipFlag isAdvanced) {
        Badge badge = this.badge.get();
        ItemHelper.getOptional(stack, PetrolparkDataComponentTypes.BADGE_AWARD).ifPresentOrElse(badgeAward -> {
            tooltipComponents.add(badge.getDescription().copy().setStyle(PRIMARY));
            Level level = context.level();
            if (level != null) tooltipComponents.add(Component.translatable("item.petrolpark.badge.awarded", Optional.ofNullable(level.getPlayerByUUID(badgeAward.playerUUID())).map(Player::getDisplayName).map(Component::copy).orElse(Component.literal("unknown")).setStyle(HIGHLIGHT), Component.literal(df.format(new Date(badgeAward.awardDate()))).setStyle(HIGHLIGHT)).setStyle(PRIMARY));
        }, () -> 
            tooltipComponents.add(Component.translatable("item.petrolpark.badge.unknown").withStyle(ChatFormatting.GRAY))
        );

    };

    @Override
    public ItemStack getCraftingRemainingItem(@Nonnull ItemStack stack) {
        return stack;
    };

    @Override
    public boolean isFoil(@Nonnull ItemStack pStack) {
        return true;
    };

    public static record BadgeAward(UUID playerUUID, long awardDate) {

        public static final Codec<BadgeAward> CODEC = RecordCodecBuilder.create(instance -> 
            instance.group(
                UUIDUtil.CODEC.fieldOf("player").forGetter(BadgeAward::playerUUID),
                Codec.LONG.fieldOf("date").forGetter(BadgeAward::awardDate)
            ).apply(instance, BadgeAward::new)
        );

        public static final StreamCodec<ByteBuf, BadgeAward> STREAM_CODEC = StreamCodec.composite(
            UUIDUtil.STREAM_CODEC, BadgeAward::playerUUID,
            ByteBufCodecs.VAR_LONG, BadgeAward::awardDate,
            BadgeAward::new
        );

        @Override
        public final boolean equals(Object obj) {
            if (this == obj) return true;
            return obj instanceof BadgeAward award && playerUUID.equals(award.playerUUID) && awardDate == award.awardDate;
        };

        @Override
        public final int hashCode() {
            return playerUUID.hashCode() ^ (int)awardDate;
        };
    };
    
};
