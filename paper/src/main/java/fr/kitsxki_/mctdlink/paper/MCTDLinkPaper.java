package fr.kitsxki_.mctdlink.paper;

import fr.kitsxki_.mctdlink.common.api.ConfigurationService;
import fr.kitsxki_.mctdlink.common.api.DatabasesService;
import fr.kitsxki_.mctdlink.common.impl.ConfigurationServiceImpl;
import fr.kitsxki_.mctdlink.common.impl.databases.DatabasesServiceImpl;
import fr.kitsxki_.mctdlink.common.impl.databases.models.DatabasesConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

public class MCTDLinkPaper extends JavaPlugin {

    @Nullable
    private DatabasesService databasesService;

    public MCTDLinkPaper() {
    }

    @Override
    public void onEnable() {
        final @NotNull ConfigurationService configurationService = new ConfigurationServiceImpl();
        final @NotNull DatabasesConfiguration databasesConfiguration;
        try {
            final @NotNull Path dataFolder = this.getDataFolder().toPath();
            if(!Files.exists(dataFolder))
                Files.createDirectories(dataFolder);

            databasesConfiguration = configurationService.copyAndLoadJson(
                    Objects.requireNonNull(this.getResource("config.json")),
                    dataFolder.resolve("config.json"),
                    DatabasesConfiguration.class
            );
        } catch (final @NotNull IOException e) {
            throw new RuntimeException(e);
        }

        this.databasesService = new DatabasesServiceImpl(databasesConfiguration);
        this.databasesService.initDatabases();
    }

    @Override
    public void onDisable() {
        if(this.databasesService != null)
            this.databasesService.disableDatabases();
    }
}
