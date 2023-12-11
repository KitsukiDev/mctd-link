package fr.kitsxki_.mctdlink.common.api;

import fr.kitsxki_.mctdlink.common.impl.link.models.CodeResult;
import fr.kitsxki_.mctdlink.common.impl.link.models.LinkResult;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface LinkService {

    @NotNull
    CompletableFuture<CodeResult> getLinkCode(final @NotNull String discordId, final String discordName);

    @NotNull
    CompletableFuture<LinkResult> linkPlayer(final @NotNull UUID minecraftId, final @NotNull String minecraftName, final @NotNull String code);

    @NotNull
    CompletableFuture<String> generateRandomCode();
}
