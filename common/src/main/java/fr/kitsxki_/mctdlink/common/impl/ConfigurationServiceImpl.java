package fr.kitsxki_.mctdlink.common.impl;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import fr.kitsxki_.mctdlink.common.api.ConfigurationService;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class ConfigurationServiceImpl implements ConfigurationService {

    @NotNull
    private final ObjectMapper mapper;

    public ConfigurationServiceImpl() {
        final @NotNull ObjectMapper mapper = new ObjectMapper();
        mapper.enable(JsonParser.Feature.ALLOW_COMMENTS);
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        mapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
        this.mapper = mapper;
    }

    @Override
    public <T> T loadJson(final @NotNull Path file, final @NotNull Class<T> clazz) throws IOException {
        return this.mapper.readValue(Files.newInputStream(file), clazz);
    }

    @Override
    public void copyIfNotExists(@NotNull InputStream source, @NotNull Path destination) throws IOException {
        if(!Files.exists(destination))
            Files.copy(source, destination);
    }
}
