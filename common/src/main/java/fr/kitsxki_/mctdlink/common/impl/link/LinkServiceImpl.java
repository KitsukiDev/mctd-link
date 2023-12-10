package fr.kitsxki_.mctdlink.common.impl.link;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import fr.kitsxki_.mctdlink.common.api.DatabasesService;
import fr.kitsxki_.mctdlink.common.api.LinkService;
import fr.kitsxki_.mctdlink.common.api.LinkedPlayerService;
import fr.kitsxki_.mctdlink.common.impl.link.models.CodeResult;
import fr.kitsxki_.mctdlink.common.impl.link.models.LinkResult;
import fr.kitsxki_.mctdlink.common.impl.players.models.LinkedPlayer;
import org.bson.Document;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public final class LinkServiceImpl implements LinkService {

    @NotNull
    public static final String LINKED_PLAYERS_COLLECTION = "linked_players";
    @NotNull
    public static final String TEMPORARY_CODES_COLLECTION = "temp_codes";

    @NotNull
    private final LinkedPlayerService linkedPlayerService;
    @NotNull
    private final MongoDatabase database;

    public LinkServiceImpl(final @NotNull LinkedPlayerService linkedPlayerService, final @NotNull DatabasesService databasesService) {
        this.linkedPlayerService = linkedPlayerService;
        this.database = databasesService.getMongoDB().getDatabase();
    }

    @Override
    @NotNull
    public CompletableFuture<CodeResult> getLinkCode(final @NotNull String discordId) {
        return CompletableFuture.supplyAsync(() -> {
            final @NotNull Optional<LinkedPlayer> linkedPlayerOpt = this.linkedPlayerService.getPlayer(discordId);
            if(linkedPlayerOpt.isPresent())
                return new CodeResult.AlreadyLinked(linkedPlayerOpt.get().getMinecraftName());

            return new CodeResult.Created("");
        });
    }

    @Override
    @NotNull
    public CompletableFuture<LinkResult> linkPlayer(final @NotNull UUID minecraftId, final @NotNull String minecraftName, final @NotNull String code) {
        return CompletableFuture.supplyAsync(() -> {
            final @NotNull Optional<LinkedPlayer> linkedPlayerOpt = this.linkedPlayerService.getPlayer(minecraftId);
            if(linkedPlayerOpt.isPresent())
                return new LinkResult.AlreadyLinked(linkedPlayerOpt.get().getMinecraftName());

            final @NotNull MongoCollection<Document> tempCodes = this.database.getCollection(TEMPORARY_CODES_COLLECTION);
            final @Nullable Document jsonTempCode = tempCodes.find(new Document("temp_code", code)).first();
            if(jsonTempCode == null)
                return new LinkResult.InvalidCode(code);

            final @NotNull String discordName = jsonTempCode.getString("discord_name");

            final @NotNull Document link = new Document();
            link.put("minecraft_id", minecraftId);
            link.put("minecraft_name", minecraftName);
            link.put("discord_id", jsonTempCode.getString("discord_id"));
            link.put("discord_name", discordName);

            final @NotNull MongoCollection<Document> linkedPlayers = this.database.getCollection(LINKED_PLAYERS_COLLECTION);
            linkedPlayers.insertOne(link);

            try {
                this.linkedPlayerService.registerPlayer(link).get(5, TimeUnit.SECONDS);
            } catch (InterruptedException | ExecutionException | TimeoutException e) {
                throw new RuntimeException(e);
            }

            return new LinkResult.Linked(discordName);
        });
    }
}
