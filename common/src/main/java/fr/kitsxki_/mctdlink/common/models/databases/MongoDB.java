package fr.kitsxki_.mctdlink.common.models.databases;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import fr.kitsxki_.mctdlink.common.impl.databases.models.credentials.MongoDBCredentials;
import fr.kitsxki_.mctdlink.common.models.MCTDLogger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MongoDB {

    @NotNull
    private final MongoClientSettings settings;
    @NotNull
    private final MCTDLogger logger;

    @Nullable
    private MongoClient client;

    public MongoDB(final @NotNull MongoDBCredentials credentials) {
        final @NotNull MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(new ConnectionString(credentials.toConnectionString()))
                .build();

        this.settings = settings;
        this.logger = new MCTDLogger("MongoDB");
    }

    public void initConnection() {
        if(this.client != null)
            return;

        new Thread(() -> this.client = MongoClients.create(this.settings), "MongoDB");
        this.logger.info("Successfully initialized the MongoDB connection.");
    }

    public void closeConnection() {
        if(this.client == null)
            return;

        this.client.close();
        this.client = null;
    }

    @Nullable
    public MongoClient getClient() {
        return this.client;
    }
}
