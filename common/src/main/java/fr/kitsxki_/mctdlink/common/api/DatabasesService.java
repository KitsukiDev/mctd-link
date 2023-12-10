package fr.kitsxki_.mctdlink.common.api;

import fr.kitsxki_.mctdlink.common.models.MongoDB;
import fr.kitsxki_.mctdlink.common.models.Redis;
import org.jetbrains.annotations.NotNull;

public interface DatabasesService {

    void initDatabases();
    void disableDatabases();

    @NotNull
    Redis getRedis();
    @NotNull
    MongoDB getMongoDB();
}
