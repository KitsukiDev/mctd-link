package fr.kitsxki_.mctdlink.common.impl.link.models;

import org.jetbrains.annotations.NotNull;

public class LinkResult {

    @NotNull
    private final String name;
    @NotNull
    private final String message;

    public LinkResult(final @NotNull String name, final @NotNull String message) {
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

    public static final class Linked extends LinkResult {

        public Linked(final @NotNull String discordAccount) {
            super("Linked", String.format("Your account has successfully been linked to the %s Discord account!", discordAccount));
        }
    }

    public static final class AlreadyLinked extends LinkResult {

        public AlreadyLinked(final @NotNull String discordAccount) {
            super("Already linked", String.format("Your account is already linked to the %s Discord account!", discordAccount));
        }
    }

    public static final class InvalidCode extends LinkResult {

        public InvalidCode(final @NotNull String code) {
            super("Invalid code", String.format("The %s code is invalid!", code));
        }
    }
}
