package fr.kitsxki_.mctdlink.common.impl.databases.models;

import fr.kitsxki_.mctdlink.common.impl.databases.models.credentials.MongoDBCredentials;
import fr.kitsxki_.mctdlink.common.impl.databases.models.credentials.RedisCredentials;
import org.jetbrains.annotations.NotNull;

public class DatabasesConfiguration {

    @NotNull
    public RedisCredentials redis;

    @NotNull
    public MongoDBCredentials mongodb;
}
