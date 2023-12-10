package fr.kitsxki_.mctdlink.common.impl.databases.models.credentials;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class RedisCredentials {

    @NotNull
    private final String host;
    @Nullable
    private final String username;
    @Nullable
    private final String password;
    private final int database;
    private final int port;

    public RedisCredentials(final @JsonProperty("host") @NotNull String host, final @JsonProperty("username") @Nullable String username, final @JsonProperty("password") @Nullable String password, final @JsonProperty("database") int database, final @JsonProperty("port") int port) {
        this.host = host;
        this.username = username;
        this.password = password;
        this.database = database;
        this.port = port;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final @NotNull RedisCredentials that = (RedisCredentials) o;
        return this.database == that.database && this.port == that.port && Objects.equals(this.host, that.host) && Objects.equals(this.username, that.username) && Objects.equals(this.password, that.password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.host, this.username, this.password, this.database, this.port);
    }

    @Override
    public String toString() {
        return "RedisCredentials{" +
                "host='" + this.host + '\'' +
                ", username='" + this.username + '\'' +
                ", password='" + this.password + '\'' +
                ", database=" + this.database +
                ", port=" + this.port +
                '}';
    }

    @NotNull
    public String getHost() {
        return this.host;
    }


    @Nullable
    public String getUsername() {
        return this.username;
    }

    @Nullable
    public String getPassword() {
        return this.password;
    }

    public int getDatabase() {
        return this.database;
    }

    public int getPort() {
        return this.port;
    }
}
