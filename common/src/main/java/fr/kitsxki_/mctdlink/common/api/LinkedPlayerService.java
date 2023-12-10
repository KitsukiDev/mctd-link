package fr.kitsxki_.mctdlink.common.api;

import fr.kitsxki_.mctdlink.common.impl.players.models.LinkedPlayer;
import org.bson.Document;
import org.jetbrains.annotations.NotNull;
import org.redisson.api.RFuture;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface LinkedPlayerService {

    @NotNull
    CompletableFuture<Boolean> registerPlayer(final @NotNull Document document);

    @NotNull
    RFuture<Long> unregisterPlayer(final @NotNull UUID minecraftId);

    @NotNull
    Optional<LinkedPlayer> getPlayer(final @NotNull UUID minecraftId);

    @NotNull
    Optional<LinkedPlayer> getPlayer(final @NotNull String discordId);

    @NotNull
    List<LinkedPlayer> getPlayers();

    @NotNull
    Map<UUID, LinkedPlayer> getMinecraftPlayerMap();
}
