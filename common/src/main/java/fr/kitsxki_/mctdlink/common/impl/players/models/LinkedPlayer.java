package fr.kitsxki_.mctdlink.common.impl.players.models;

import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public final class LinkedPlayer {

    @NotNull
    private final UUID minecraftId;
    @NotNull
    private final String minecraftName;
    @NotNull
    private final String discordId;
    @NotNull
    private final String discordName;

    public LinkedPlayer(
            final @NotNull UUID minecraftId,
            final @NotNull String minecraftName,
            final @NotNull String discordId,
            final @NotNull String discordName
    ) {
        this.minecraftId = minecraftId;
        this.minecraftName = minecraftName;
        this.discordId = discordId;
        this.discordName = discordName;
    }

    @NotNull
    public UUID getMinecraftId() {
        return this.minecraftId;
    }

    @NotNull
    public String getMinecraftName() {
        return this.minecraftName;
    }

    @NotNull
    public String getDiscordId() {
        return this.discordId;
    }

    @NotNull
    public String getDiscordName() {
        return this.discordName;
    }
}
