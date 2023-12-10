package fr.kitsxki_.mctdlink.common.models;

import org.jetbrains.annotations.NotNull;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class MCTDLogger {

    @NotNull
    private static final String ZONED_ID = "Europe/Paris";
    @NotNull
    private static final String PATTERN = "HH:mm:ss";
    @NotNull
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(PATTERN);

    @NotNull
    private final String prefix;

    public MCTDLogger(final @NotNull String prefix) {
        this.prefix = prefix;
    }

    public void info(final @NotNull String info) {
        this.log("INFO", info);
    }

    public void warn(final @NotNull String warning) {
        this.log("WARNING", warning);
    }

    public void severe(final @NotNull String severe) {
        this.log("SEVERE", severe);
    }

    private void log(final @NotNull String level, final @NotNull String log) {
        final String dateTime = ZonedDateTime.now(ZoneId.of(ZONED_ID)).format(DATE_TIME_FORMATTER);
        System.out.println("[" + dateTime + "] " + "[" + this.prefix + "] " + level + " " + log);
    }
}
