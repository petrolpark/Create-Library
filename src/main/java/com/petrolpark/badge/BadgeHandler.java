package com.petrolpark.badge;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.petrolpark.Petrolpark;
import com.simibubi.create.foundation.utility.Pair;

import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber
public class BadgeHandler {

    public static final String VERSION_UUID = "534319cb-3ba5-4c9f-befa-edb5e9562ba8";

    public static final String GET_BADGES_URL = "\\";

    public static final String EARLY_BIRD_URL = "https://us-central1.gcp.data.mongodb-api.com/app/destroybadges-qojlw/endpoint/AddEarlyBirdToMinecraftUUID";

    public static final Duration HTTP_TIMEOUT = Duration.ofSeconds(10);
    
    public static void getAndAddBadges(ServerPlayer player) {
        HttpClient client = HttpClient.newHttpClient();

        String getBadgesJsonInputString = "{\"uuid\": \""+getFormattedUUID(player)+"\"}";

        HttpRequest getBadgesRequest = HttpRequest.newBuilder()
                .uri(URI.create(GET_BADGES_URL))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(getBadgesJsonInputString))
                .timeout(HTTP_TIMEOUT)
                .build();

        CompletableFuture<HttpResponse<InputStream>> responseFuture = client.sendAsync(getBadgesRequest, HttpResponse.BodyHandlers.ofInputStream());

        responseFuture.thenAcceptAsync(response -> {
            try (
                BufferedReader reader = new BufferedReader(new InputStreamReader(response.body()));
            ) {
                List<Pair<Badge, Date>> badges = new ArrayList<>();
                JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();
                for (JsonElement element : json.getAsJsonArray("badges")) {
                    JsonObject badgeObject = element.getAsJsonObject();
                    String date = badgeObject.get("date").getAsString();
                    date = date.substring(0, date.length() - 1);
                    Badge badge = Badge.getBadge(badgeObject.get("namespace").getAsString(), badgeObject.get("id").getAsString());
                    Petrolpark.LOGGER.info(badgeObject.get("namespace").getAsString(), badgeObject.get("id").getAsString());
                    if (badge != null) {
                        badges.add(Pair.of(
                            badge,
                            Date.from(LocalDateTime.parse(date).toInstant(ZoneOffset.UTC))
                        ));
                    };
                };
                player.getCapability(BadgesCapability.Provider.PLAYER_BADGES).ifPresent(playerBadges -> {
                    playerBadges.setBadges(badges);
                    // Award Advancements for Badges
                    playerBadges.getBadges().forEach(pair ->
                        pair.getFirst().grantAdvancement(player));
                });
            } catch (Exception e) {};
        }).join();

    };

    public static void fetchAndAddBadgesIncludingEarlyBird(ServerPlayer player) {
        try {
            if (isEarlyBirdViable()) { // Try and give this Player the Early bird badge
                HttpClient client = HttpClient.newHttpClient();

                String addEarlyBirdJsonInputString = "{\"uuid\": \""+getFormattedUUID(player)+"\", \"version_uuid\": \""+VERSION_UUID+"\"}";

                HttpRequest getBadgesRequest = HttpRequest.newBuilder()
                    .uri(URI.create(EARLY_BIRD_URL))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(addEarlyBirdJsonInputString))
                    .build();

                CompletableFuture<HttpResponse<String>> responseFuture = client.sendAsync(getBadgesRequest, HttpResponse.BodyHandlers.ofString());

                responseFuture.thenAcceptAsync(response -> {
                    getAndAddBadges(player);
                }).join();
            
            } else { // If Early Bird is no longer obtainable, get the Badges straight away
                getAndAddBadges(player);
            };
        } catch (Throwable e) {
            Petrolpark.LOGGER.error("Error fetching Badges for player: ", e);
        };

    };

    @SubscribeEvent
    public static void onPlayerEntersWorld(PlayerEvent.PlayerLoggedInEvent event) {
        // Collect the Player's badges
        if (!event.getEntity().level().isClientSide() && event.getEntity() instanceof ServerPlayer sp) fetchAndAddBadgesIncludingEarlyBird(sp);
    };

    private static boolean isEarlyBirdViable() {
        return false;
    };

    private static String getFormattedUUID(ServerPlayer player) {
        return player.getGameProfile().getId().toString().replace("-", "");
    };
    
};
