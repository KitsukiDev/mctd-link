package fr.kitsxki_.mctdlink.common.impl.players;

import fr.kitsxki_.mctdlink.common.api.DatabasesService;
import fr.kitsxki_.mctdlink.common.api.LinkedPlayerService;
import fr.kitsxki_.mctdlink.common.impl.players.models.LinkedPlayer;
import org.bson.Document;
import org.jetbrains.annotations.NotNull;
import org.redisson.api.RFuture;
import org.redisson.api.RMapCache;
import org.redisson.api.RedissonClient;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public final class LinkedPlayerServiceImpl implements LinkedPlayerService {

    @NotNull
    public static final String MINECRAFT_PLAYER_MAP_KEY = "linked_players:minecraft";
    @NotNull
    public static final String DISCORD_PLAYER_MAP_KEY = "linked_players:minecraft";

    @NotNull
    private final RMapCache<UUID, LinkedPlayer> minecraftPlayerMap;
    @NotNull
    private final RMapCache<String, LinkedPlayer> discordPlayerMap;

    public LinkedPlayerServiceImpl(final @NotNull DatabasesService databasesService) {
        final @NotNull RedissonClient client = databasesService.getRedis().getClient();
        this.minecraftPlayerMap = client.getMapCache(MINECRAFT_PLAYER_MAP_KEY);
        this.discordPlayerMap = client.getMapCache(DISCORD_PLAYER_MAP_KEY);
    }

    @Override
    @NotNull
    public CompletableFuture<Boolean> registerPlayer(final @NotNull Document document) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                final boolean mcCf = this.minecraftPlayerMap.fastPutAsync((UUID) document.get("minecraft_id"), new LinkedPlayer(
                        (UUID) document.get("minecraft_id"),
                        document.getString(""),
                        document.getString(""),
                        document.getString("")
                )).get();

                final boolean dscCf = this.minecraftPlayerMap.fastPutAsync((UUID) document.get("minecraft_id"), new LinkedPlayer(
                        (UUID) document.get("minecraft_id"),
                        document.getString(""),
                        document.getString(""),
                        document.getString("")
                )).get();

                if(mcCf != dscCf)
                    throw new IllegalStateException("FATAL Minecraft and Discord links are out of sync!");

                return mcCf;
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    @NotNull
    public RFuture<Long> unregisterPlayer(final @NotNull UUID minecraftId) {
        return this.minecraftPlayerMap.fastRemoveAsync(minecraftId);
    }

    @Override
    @NotNull
    public Optional<LinkedPlayer> getPlayer(final @NotNull UUID uniqueId) {
        return Optional.ofNullable(this.minecraftPlayerMap.getOrDefault(uniqueId, null));
    }

    @Override
    @NotNull
    public Optional<LinkedPlayer> getPlayer(final @NotNull String discordId) {
        return Optional.empty();
    }

    @Override
    @NotNull
    public List<LinkedPlayer> getPlayers() {
        return new ArrayList<>(this.minecraftPlayerMap.values());
    }

    @Override
    @NotNull
    public Map<UUID, LinkedPlayer> getMinecraftPlayerMap() {
        return this.minecraftPlayerMap;
    }
}
