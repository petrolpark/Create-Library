package petrolpark.mc.library.core.badge;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import petrolpark.mc.library.Petrolpark;
import petrolpark.mc.library.registry.PetrolparkAttachmentTypes;
import petrolpark.mc.library.registry.PetrolparkCriteriaTriggers;

@EventBusSubscriber
public class BadgeHandler {

    public static final String VERSION_UUID = "fae762ea-6650-4cce-830a-3c05bd20e042";

    public static final String GET_BADGES_URL = "https://badges.petrolpark.co.uk/minecraft/get_badges_of_uuid";
    public static final String EARLY_BIRD_URL = "https://badges.petrolpark.co.uk/minecraft/try_add_early_bird_to_uuid";
    
    public static void getAndAddBadges(ServerPlayer player) {
        HttpClient client = HttpClient.newHttpClient();

        String getBadgesJsonInputString = "{\"uuid\": \""+getFormattedUUID(player)+"\"}";

        HttpRequest getBadgesRequest = HttpRequest.newBuilder()
                .uri(URI.create(GET_BADGES_URL))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(getBadgesJsonInputString))
                .build();

        CompletableFuture<HttpResponse<InputStream>> responseFuture = client.sendAsync(getBadgesRequest, HttpResponse.BodyHandlers.ofInputStream());

        responseFuture.thenAcceptAsync(response -> {
            try (
                BufferedReader reader = new BufferedReader(new InputStreamReader(response.body()));
            ) {
                Map<Badge, Date> badges = new HashMap<>();
                JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();
                for (JsonElement element : json.getAsJsonArray("badges")) {
                    JsonObject badgeObject = element.getAsJsonObject();
                    String date = badgeObject.get("date").getAsString();
                    date = date.substring(0, date.length() - 1);
                    Badge badge = Badge.getBadge(badgeObject.get("namespace").getAsString(), badgeObject.get("id").getAsString());
                    if (badge != null) badges.put(badge,  Date.from(LocalDateTime.parse(date).toInstant(ZoneOffset.UTC)));
                };
                PlayerBadges playerBadges = player.getData(PetrolparkAttachmentTypes.BADGES);
                playerBadges.badges().putAll(badges);
                badges.keySet().forEach(badge -> PetrolparkCriteriaTriggers.RECEIVE_BADGE.get().trigger(player, badge));
            } catch (Exception e) {};
        });

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
                });
            
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

    private static final boolean isEarlyBirdViable() {
        return false;
    };

    private static final String getFormattedUUID(ServerPlayer player) {
        return player.getGameProfile().getId().toString().replace("-", "");
    };
    
};
