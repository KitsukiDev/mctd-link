package fr.kitsxki_.mctdlink.common.api;

import fr.kitsxki_.mctdlink.common.impl.link.models.CodeResult;
import fr.kitsxki_.mctdlink.common.impl.link.models.LinkResult;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface LinkService {

    @NotNull
    CompletableFuture<CodeResult> getLinkCode(final @NotNull String userId);

    @NotNull
    CompletableFuture<LinkResult> linkPlayer(final @NotNull UUID uniqueId, final @NotNull String name, final @NotNull String code);
}
