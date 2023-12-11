package fr.kitsxki_.mctdlink.discord.commands;

import fr.kitsxki_.mctdlink.common.api.LinkService;
import fr.kitsxki_.mctdlink.discord.annotations.CommandMethod;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LinkCommand {

    @NotNull
    private final LinkService linkService;
    @NotNull
    private final Logger logger;

    public LinkCommand(final @NotNull LinkService linkService) {
        this.linkService = linkService;
        this.logger = LoggerFactory.getLogger(this.getClass());
    }

    @CommandMethod(
            name = "link",
            description = "Retrieve your link status."
    )
    public void onLinkCommand(final @NotNull SlashCommandInteractionEvent event) {
        event.deferReply(true).queue();
        this.linkService.getLinkCode(event.getUser().getId(), event.getUser().getName())
                .thenAcceptAsync(codeResult -> codeResult.accept(event))
                .exceptionally(err -> {
                    event.getHook().sendMessage("An error has occurred while retrieving your link status!").queue();
                    err.printStackTrace();
                    return null;
                });
    }
}
