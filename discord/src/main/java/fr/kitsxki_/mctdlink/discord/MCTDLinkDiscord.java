package fr.kitsxki_.mctdlink.discord;

import fr.kitsxki_.mctdlink.common.api.ConfigurationService;
import fr.kitsxki_.mctdlink.common.api.DatabasesService;
import fr.kitsxki_.mctdlink.common.impl.ConfigurationServiceImpl;
import fr.kitsxki_.mctdlink.common.impl.databases.DatabasesServiceImpl;
import fr.kitsxki_.mctdlink.common.impl.databases.models.DatabasesConfiguration;
import fr.kitsxki_.mctdlink.discord.api.CommandsService;
import fr.kitsxki_.mctdlink.discord.impl.CommandsServiceImpl;
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
    @NotNull
    private final JDA jda;

    public MCTDLinkDiscord() throws InterruptedException {
        final @NotNull JDA jda = JDABuilder.createDefault(
                Objects.requireNonNull(System.getenv("MCTDLINK_TOKEN"), "Bot token cannot be empty!"))
                .build()
                .awaitReady();

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
        this.jda = jda;
    }

    public void init() {
        this.databasesService.initDatabases();

        final @NotNull CommandsService commandsService = new CommandsServiceImpl(this.jda);
        this.registerCommands(commandsService);

        this.jda.addEventListener(commandsService);
    }

    public void disable() {
        this.databasesService.disableDatabases();
    }

    private void registerCommands(final @NotNull CommandsService commandsService, final @NotNull Object... commands) {
        this.jda.retrieveCommands().queue(jdaCommands -> {
            jdaCommands.forEach(c -> c.delete().queue());
            commandsService.registerCommands(commands);
        });
    }
}
