package fr.kitsxki_.mctdlink.discord;

import org.jetbrains.annotations.NotNull;

public class Bootstrap {

    public static void main(final String[] args) {
        final @NotNull MCTDLinkDiscord application = new MCTDLinkDiscord();
        application.enable();

        Runtime.getRuntime().addShutdownHook(
                new Thread(
                        application::disable,
                        "Shutdown Hook"
                )
        );
    }
}
