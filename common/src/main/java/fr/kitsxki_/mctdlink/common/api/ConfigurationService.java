package fr.kitsxki_.mctdlink.common.api;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;

public interface ConfigurationService {

    default <T> T copyAndLoadJson(final @NotNull InputStream source, final @NotNull Path destination, final @NotNull Class<T> clazz) throws IOException {
        this.copyIfNotExists(source, destination);
        return this.loadJson(destination, clazz);
    }

    <T> T loadJson(final @NotNull Path file, final @NotNull Class<T> clazz) throws IOException;

    void copyIfNotExists(final @NotNull InputStream source, final @NotNull Path destination) throws IOException;
}
