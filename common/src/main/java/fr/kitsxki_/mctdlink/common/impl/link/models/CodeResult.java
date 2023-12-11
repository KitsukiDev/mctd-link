package fr.kitsxki_.mctdlink.common.impl.link.models;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class CodeResult {

    @NotNull
    private final String name;
    @NotNull
    private final Consumer<SlashCommandInteractionEvent> consumer;

    public CodeResult(final @NotNull String name, final @NotNull Consumer<SlashCommandInteractionEvent> consumer) {
        this.name = name;
        this.consumer = consumer;
    }

    public void accept(final @NotNull SlashCommandInteractionEvent event) {
        this.consumer.accept(event);
    }

    @NotNull
    public String getName() {
        return this.name;
    }

    public static final class Created extends CodeResult {

        public Created(final @NotNull String code) {
            super("Created", event -> {
                event.getHook().sendMessage(String.format("Your link code has successfully been created » %s", code)).queue();
            });
        }
    }

    public static final class Retrieved extends CodeResult {

        public Retrieved(final @NotNull String code) {
            super("Retrieved", event -> {
                event.getHook().sendMessage(String.format("Here's your link code » %s", code)).queue();
            });
        }
    }

    public static final class AlreadyLinked extends CodeResult {

        public AlreadyLinked(final @NotNull String discordAccount) {
            super("Already linked", event -> {
                event.getHook().sendMessage(String.format("Your account is already linked to the %s Discord account!", discordAccount)).queue();
            });
        }
    }
}
