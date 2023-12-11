package fr.kitsxki_.mctdlink.common.impl.players;

import fr.kitsxki_.mctdlink.common.api.DatabasesService;
import fr.kitsxki_.mctdlink.common.api.LinkedPlayerService;
import fr.kitsxki_.mctdlink.common.impl.players.models.LinkedEntry;
import org.bson.Document;
import org.jetbrains.annotations.NotNull;
import org.redisson.api.RMapCache;
import org.redisson.api.RedissonClient;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public final class LinkedEntryServiceImpl implements LinkedPlayerService {

    @NotNull
    public static final String PLAYER_MAP_KEY = "linked_players";
    @NotNull
    public static final String UNIQUE_ID_MAP_KEY = "unique_ids";

    @NotNull
    private final RMapCache<UUID, LinkedEntry> playerMap;
    @NotNull
    private final RMapCache<String, UUID> uniqueIdMap;

    public LinkedEntryServiceImpl(final @NotNull DatabasesService databasesService) {
        final @NotNull RedissonClient client = databasesService.getRedis().getClient();
        this.playerMap = client.getMapCache(PLAYER_MAP_KEY);
        this.uniqueIdMap = client.getMapCache(UNIQUE_ID_MAP_KEY);
    }

    @Override
    @NotNull
    public CompletableFuture<Boolean> registerPlayer(final @NotNull Document document) {
        return CompletableFuture.supplyAsync(() -> {
            final @NotNull LinkedEntry linkedEntry = new LinkedEntry(
                    (UUID) document.get("minecraftId"),
                    document.getString("minecraftName"),
                    document.getString("discordId"),
                    document.getString("discordName")
            );

            try {
                this.uniqueIdMap.fastPutAsync(linkedEntry.getDiscordId(), linkedEntry.getMinecraftId()).get(5, TimeUnit.SECONDS);
                return this.playerMap.fastPutAsync((UUID) document.get("minecraftId"), linkedEntry).get(5, TimeUnit.SECONDS);
            } catch (InterruptedException | ExecutionException | TimeoutException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    @NotNull
    public CompletableFuture<Boolean> unregisterPlayer(final @NotNull UUID minecraftId) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                final @NotNull LinkedEntry linkedEntry = this.playerMap.removeAsync(minecraftId).get(5, TimeUnit.SECONDS);
                return this.uniqueIdMap.fastRemoveAsync(linkedEntry.getDiscordId()).get(5, TimeUnit.SECONDS) > 1L;
            } catch (InterruptedException | ExecutionException | TimeoutException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    @NotNull
    public CompletableFuture<LinkedEntry> getPlayer(final @NotNull UUID uniqueId) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return this.playerMap.getAsync(uniqueId).get(5, TimeUnit.SECONDS);
            } catch (InterruptedException | ExecutionException | TimeoutException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    @NotNull
    public CompletableFuture<LinkedEntry> getPlayer(final @NotNull String discordId) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return this.uniqueIdMap.containsKeyAsync(discordId).get() ? this.playerMap.getAsync(
                        this.uniqueIdMap.getAsync(discordId).get(5, TimeUnit.SECONDS)
                ).get(5, TimeUnit.SECONDS) : null;
            } catch (InterruptedException | ExecutionException | TimeoutException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    @NotNull
    public List<LinkedEntry> getPlayers() {
        return new ArrayList<>(this.playerMap.values());
    }

    @Override
    @NotNull
    public Map<UUID, LinkedEntry> getPlayerMap() {
        return this.playerMap;
    }
}
