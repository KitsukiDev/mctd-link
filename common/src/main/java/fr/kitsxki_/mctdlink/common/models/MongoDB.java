package fr.kitsxki_.mctdlink.common.models;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import fr.kitsxki_.mctdlink.common.impl.databases.models.credentials.MongoDBCredentials;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.redisson.client.RedisConnectionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MongoDB {

    @NotNull
    private final MongoDBCredentials credentials;
    @NotNull
    private final Logger logger;

    @Nullable
    private MongoClient client;
    @Nullable
    private MongoDatabase database;

    public MongoDB(final @NotNull MongoDBCredentials credentials) {
        this.credentials = credentials;
        this.logger = LoggerFactory.getLogger(this.getClass());
    }

    public void initConnection() {
        if(this.client != null)
            return;

        final @NotNull MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(new ConnectionString(credentials.toConnectionString()))
                .build();

        /*new Thread(() -> {
            final @NotNull MongoClient client = MongoClients.create(settings);
            this.client = client;
            this.database = client.getDatabase(this.credentials.getDatabase());
        }, "MongoDB");*/
        final @NotNull MongoClient client = MongoClients.create(settings);
        this.client = client;
        this.database = client.getDatabase(this.credentials.getDatabase());
        this.logger.info("Successfully initialized the MongoDB connection.");
    }

    public void closeConnection() {
        if(this.client == null)
            return;

        this.client.close();
        this.client = null;
        this.database = null;
        this.logger.info("Successfully closed the MongoDB connection.");
    }

    @NotNull
    public MongoClient getClient() {
        if(this.client == null)
            throw new RedisConnectionException("The MongoDB connection is not initialized yet!");

        return this.client;
    }

    @NotNull
    public MongoDatabase getDatabase() {
        if(this.database == null)
            throw new RedisConnectionException("The MongoDB connection is not initialized yet!");

        return this.database;
    }
}
