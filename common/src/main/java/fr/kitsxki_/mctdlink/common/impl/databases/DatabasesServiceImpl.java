package fr.kitsxki_.mctdlink.common.impl.databases;

import fr.kitsxki_.mctdlink.common.api.DatabasesService;
import fr.kitsxki_.mctdlink.common.models.databases.Redis;
import fr.kitsxki_.mctdlink.common.impl.databases.models.DatabasesConfiguration;
import org.jetbrains.annotations.NotNull;

public class DatabasesServiceImpl implements DatabasesService {

    @NotNull
    private final Redis redis;

    public DatabasesServiceImpl(final @NotNull DatabasesConfiguration databasesConfiguration) {
        this.redis = new Redis(databasesConfiguration.redis);
    }

    @Override
    public void initDatabases() {
        new Thread(this.redis::initConnection, "Redis").start();
    }

    @Override
    public void disableDatabases() {
        this.redis.closeConnection();
    }

    @NotNull
    public Redis getRedis() {
        return this.redis;
    }
}
