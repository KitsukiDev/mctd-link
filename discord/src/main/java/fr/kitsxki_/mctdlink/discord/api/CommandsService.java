package fr.kitsxki_.mctdlink.discord.api;

import org.jetbrains.annotations.NotNull;

public interface CommandsService {

    void registerCommand(final @NotNull Object command);
    void registerCommands(final @NotNull Object... commands);
}
