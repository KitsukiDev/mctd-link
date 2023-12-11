package fr.kitsxki_.mctdlink.common.impl.link;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import fr.kitsxki_.mctdlink.common.api.DatabasesService;
import fr.kitsxki_.mctdlink.common.api.LinkService;
import fr.kitsxki_.mctdlink.common.api.LinkedPlayerService;
import fr.kitsxki_.mctdlink.common.impl.link.models.CodeResult;
import fr.kitsxki_.mctdlink.common.impl.link.models.LinkResult;
import fr.kitsxki_.mctdlink.common.impl.players.models.LinkedEntry;
import org.bson.Document;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.SecureRandom;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public final class LinkServiceImpl implements LinkService {

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    /*
     * Collections keys
     */
    @NotNull
    public static final String LINKED_PLAYERS_COLLECTION = "linked_layers";
    @NotNull
    public static final String TEMPORARY_CODES_COLLECTION = "temp_codes";

    /*
     * temp_code entries only keys
     */
    @NotNull
    public static final String TEMP_CODE_KEY = "tempCode";

    /*
     * Common entries Discord keys
     */
    @NotNull
    public static final String DISCORD_ID_KEY = "discordId";
    @NotNull
    public static final String DISCORD_NAME_KEY = "discordName";

    /*
     * Common entries Minecraft keys
     */
    @NotNull
    public static final String MINECRAFT_ID_KEY = "minecraftId";
    @NotNull
    public static final String MINECRAFT_NAME_KEY = "minecraftName";

    @NotNull
    private final LinkedPlayerService linkedPlayerService;
    @NotNull
    private final MongoDatabase database;
    @NotNull
    private final Logger logger;

    public LinkServiceImpl(final @NotNull LinkedPlayerService linkedPlayerService, final @NotNull DatabasesService databasesService) {
        this.linkedPlayerService = linkedPlayerService;
        this.database = databasesService.getMongoDB().getDatabase();
        this.logger = LoggerFactory.getLogger(this.getClass());
    }

    @Override
    @NotNull
    public CompletableFuture<CodeResult> getLinkCode(final @NotNull String discordId, final @NotNull String discordName) {
        return CompletableFuture.supplyAsync(() -> {
            final @NotNull LinkedEntry linkedEntry;
            try {
                linkedEntry = this.linkedPlayerService.getPlayer(discordId).get(5, TimeUnit.SECONDS);
            } catch (InterruptedException | ExecutionException | TimeoutException e) {
                throw new RuntimeException(e);
            }

            if(linkedEntry != null)
                return new CodeResult.AlreadyLinked(linkedEntry.getMinecraftName());

            final @NotNull MongoCollection<Document> tempCodes = this.database.getCollection(TEMPORARY_CODES_COLLECTION);

            final @Nullable Document jsonTempCodeQuery = tempCodes.find(new Document(DISCORD_ID_KEY, discordId)).first();
            if(jsonTempCodeQuery != null)
                return new CodeResult.Retrieved(jsonTempCodeQuery.getString(TEMP_CODE_KEY));

            final @NotNull String tempCode;
            try {
                tempCode = this.generateRandomCode().get(5, TimeUnit.SECONDS);
            } catch (InterruptedException | ExecutionException | TimeoutException e) {
                throw new RuntimeException(e);
            }

            final @NotNull Document jsonTempCode = new Document();
            jsonTempCode.put(DISCORD_ID_KEY, discordId);
            jsonTempCode.put(DISCORD_NAME_KEY, discordName);
            jsonTempCode.put(TEMP_CODE_KEY, tempCode);

            tempCodes.insertOne(jsonTempCode);

            return new CodeResult.Created(tempCode);
        });
    }

    @Override
    @NotNull
    public CompletableFuture<LinkResult> linkPlayer(final @NotNull UUID minecraftId, final @NotNull String minecraftName, final @NotNull String code) {
        return CompletableFuture.supplyAsync(() -> {
            final @NotNull LinkedEntry linkedEntry;
            try {
                linkedEntry = this.linkedPlayerService.getPlayer(minecraftId).get(5, TimeUnit.SECONDS);
            } catch (InterruptedException | ExecutionException | TimeoutException e) {
                throw new RuntimeException(e);
            }

            if(linkedEntry != null)
                return new LinkResult.AlreadyLinked(linkedEntry.getMinecraftName());

            final @NotNull MongoCollection<Document> tempCodes = this.database.getCollection(TEMPORARY_CODES_COLLECTION);
            final @Nullable Document jsonTempCode = tempCodes.find(new Document(TEMP_CODE_KEY, code)).first();
            if(jsonTempCode == null)
                return new LinkResult.InvalidCode(code);

            final @NotNull String discordName = jsonTempCode.getString(DISCORD_NAME_KEY);

            final @NotNull Document link = new Document();
            link.put(DISCORD_ID_KEY, jsonTempCode.getString(DISCORD_ID_KEY));
            link.put(DISCORD_NAME_KEY, discordName);
            link.put(MINECRAFT_ID_KEY, minecraftId);
            link.put(MINECRAFT_NAME_KEY, minecraftName);

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

    @Override
    @NotNull
    public CompletableFuture<String> generateRandomCode() {
        return CompletableFuture.supplyAsync(() -> {
            final @NotNull SecureRandom random = new SecureRandom();

            final @NotNull StringBuilder code = new StringBuilder();
            for(int i = 0; i < 16; i++) {
                final int randomIndex = random.nextInt(CHARACTERS.length());
                final char randomChar = CHARACTERS.charAt(randomIndex);
                code.append(randomChar);
            }

            this.logger.info(String.format("Successfully generated a new random code: %s", code));
            return code.toString();
        });
    }
}
