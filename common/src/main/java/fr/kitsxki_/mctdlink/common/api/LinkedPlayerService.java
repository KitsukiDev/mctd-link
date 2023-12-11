package fr.kitsxki_.mctdlink.common.api;

import fr.kitsxki_.mctdlink.common.impl.players.models.LinkedEntry;
import org.bson.Document;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface LinkedPlayerService {

    @NotNull
    CompletableFuture<Boolean> registerPlayer(final @NotNull Document document);

    @NotNull
    CompletableFuture<Boolean> unregisterPlayer(final @NotNull UUID minecraftId);

    @NotNull
    CompletableFuture<LinkedEntry> getPlayer(final @NotNull UUID minecraftId);

    @NotNull
    CompletableFuture<LinkedEntry> getPlayer(final @NotNull String discordId);

    @NotNull
    List<LinkedEntry> getPlayers();

    @NotNull
    Map<UUID, LinkedEntry> getPlayerMap();
}
