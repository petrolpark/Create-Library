package com.petrolpark.badge;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.annotation.Nullable;

import com.mojang.serialization.Codec;
import com.petrolpark.PetrolparkRegistries;

public record PlayerBadges(Map<Badge, Date> badges) {

    public static final PlayerBadges empty() {
        return new PlayerBadges(new HashMap<>());
    };

    public static final Codec<PlayerBadges> CODEC = Codec.unboundedMap(PetrolparkRegistries.BADGES.byNameCodec(), Codec.LONG.xmap(Date::new, Date::getTime)).xmap(PlayerBadges::new, PlayerBadges::badges);

    public Optional<Date> awardDate(@Nullable Badge badge) {
        if (badge == null) return Optional.empty();
        return Optional.ofNullable(badges.get(badge));
    };
};
