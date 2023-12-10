package fr.kitsxki_.mctdlink.common.impl.databases;

import fr.kitsxki_.mctdlink.common.api.DatabasesService;
import fr.kitsxki_.mctdlink.common.models.databases.MongoDB;
import fr.kitsxki_.mctdlink.common.models.databases.Redis;
import fr.kitsxki_.mctdlink.common.impl.databases.models.DatabasesConfiguration;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ExecutionException;

public class DatabasesServiceImpl implements DatabasesService {

    @NotNull
    private final MongoDB mongoDB;
    @NotNull
    private final Redis redis;

    public DatabasesServiceImpl(final @NotNull DatabasesConfiguration databasesConfiguration) {
        this.mongoDB = new MongoDB(databasesConfiguration.mongodb);
        this.redis = new Redis(databasesConfiguration.redis);
    }

    @Override
    public void initDatabases() {
        this.mongoDB.initConnection();
        this.redis.initConnection();
    }

    @Override
    public void disableDatabases() {
        try {
            this.mongoDB.closeConnection();
            this.redis.closeConnection();
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @NotNull
    public Redis getRedis() {
        return this.redis;
    }
}
