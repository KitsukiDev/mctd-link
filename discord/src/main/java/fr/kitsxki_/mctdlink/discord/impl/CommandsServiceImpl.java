package fr.kitsxki_.mctdlink.discord.impl;

import fr.kitsxki_.mctdlink.discord.annotations.CommandMethod;
import fr.kitsxki_.mctdlink.discord.api.CommandsService;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;

public class CommandsServiceImpl extends ListenerAdapter implements CommandsService {

    @NotNull
    private final Map<String, Map.Entry<Method, Object>> commandsMap = new HashMap<>();

    @NotNull
    private final JDA jda;
    @NotNull
    private final Logger logger;

    public CommandsServiceImpl(final @NotNull JDA jda) {
        this.jda = jda;
        this.logger = LoggerFactory.getLogger(this.getClass());
    }

    @Override
    public void registerCommand(final @NotNull Object command) {
        for(final @NotNull Method method : command.getClass().getDeclaredMethods()) {
            if(!method.isAnnotationPresent(CommandMethod.class) || method.getParameterCount() < 1
                    || method.getParameters()[0].getType() != SlashCommandInteractionEvent.class)
                continue;

            final @NotNull CommandMethod commandMethod = method.getAnnotation(CommandMethod.class);
            final @NotNull String commandName = commandMethod.name();
            this.commandsMap.put(commandName, new AbstractMap.SimpleEntry<>(method, command));
            this.jda.upsertCommand(Commands.slash(commandName, commandMethod.description())).queue();
            this.logger.info("Successfully register a new command with the name " + commandName + "!");
        }
    }

    @Override
    public void registerCommands(final @NotNull Object... commands) {
        for(final @NotNull Object command : commands)
            this.registerCommand(command);
    }

    @Override
    public void onSlashCommandInteraction(final @NotNull SlashCommandInteractionEvent event) {
        if(!this.commandsMap.containsKey(event.getName())) {
            event.reply("This command is not handled! Please contact a server administrator.").queue();
            return;
        }

        final @NotNull Map.Entry<Method, Object> command = this.commandsMap.get(event.getName());
        try {
            command.getKey().invoke(command.getValue(), event);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
}
