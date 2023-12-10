package fr.kitsxki_.mctdlink.discord;

import fr.kitsxki_.mctdlink.common.api.ConfigurationService;
import fr.kitsxki_.mctdlink.common.api.DatabasesService;
import fr.kitsxki_.mctdlink.common.impl.ConfigurationServiceImpl;
import fr.kitsxki_.mctdlink.common.impl.databases.DatabasesServiceImpl;
import fr.kitsxki_.mctdlink.common.impl.databases.models.DatabasesConfiguration;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

public final class MCTDLinkDiscord {

    @NotNull
    private final DatabasesService databasesService;

    public MCTDLinkDiscord() {
        final @NotNull JDA jda = JDABuilder.createDefault(Objects.requireNonNull(System.getenv("MCTDLINK_TOKEN"), "Bot token cannot be empty!")).build();

        final @NotNull ConfigurationService configurationService = new ConfigurationServiceImpl();
        final @NotNull DatabasesConfiguration databasesConfiguration;
        try {
            final @NotNull Path dataFolder = Paths.get("config");
            if(!Files.exists(dataFolder))
                Files.createDirectories(dataFolder);

            databasesConfiguration = configurationService.copyAndLoadJson(
                    Objects.requireNonNull(this.getClass().getResourceAsStream("/config.json")),
                    dataFolder.resolve("config.json"),
                    DatabasesConfiguration.class
            );
        } catch (final @NotNull IOException e) {
            throw new RuntimeException(e);
        }

        this.databasesService = new DatabasesServiceImpl(databasesConfiguration);
    }

    public void enable() {
        this.databasesService.initDatabases();
    }

    public void disable() {
        this.databasesService.disableDatabases();
    }
}
