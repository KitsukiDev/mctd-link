package fr.kitsxki_.mctdlink.common.models.databases;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import fr.kitsxki_.mctdlink.common.impl.databases.models.credentials.MongoDBCredentials;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MongoDB {

    @NotNull
    private final MongoClientSettings settings;
    @NotNull
    private final Logger logger;

    @Nullable
    private MongoClient client;

    public MongoDB(final @NotNull MongoDBCredentials credentials) {
        final @NotNull MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(new ConnectionString(credentials.toConnectionString()))
                .build();

        this.settings = settings;
        this.logger = LoggerFactory.getLogger(this.getClass());
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
        this.logger.info("Successfully closed the MongoDB connection.");
    }

    @Nullable
    public MongoClient getClient() {
        return this.client;
    }
}
