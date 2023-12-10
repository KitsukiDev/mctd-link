package fr.kitsxki_.mctdlink.common.impl.link.models;

import org.jetbrains.annotations.NotNull;

public class CodeResult {

    @NotNull
    private final String name;
    @NotNull
    private final String message;

    public CodeResult(final @NotNull String name, final @NotNull String message) {
        this.name = name;
        this.message = message;
    }

    @NotNull
    public String getName() {
        return this.name;
    }

    @NotNull
    public String getMessage() {
        return this.message;
    }

    public static final class Created extends CodeResult {

        public Created(final @NotNull String code) {
            super("Created", String.format("Your link code has successfully been created » %s", code));
        }
    }

    public static final class Retrieved extends CodeResult {

        public Retrieved(final @NotNull String code) {
            super("Retrieved", String.format("Here's your link code » %s", code));
        }
    }

    public static final class AlreadyLinked extends CodeResult {

        public AlreadyLinked(final @NotNull String discordAccount) {
            super("Already linked", String.format("Your account is already linked to the %s Discord account!", discordAccount));
        }
    }
}
