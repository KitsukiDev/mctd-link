package fr.kitsxki_.mctdlink.common.impl.databases.models.credentials;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class MongoDBCredentials {

    @NotNull
    private final String host;
    @NotNull
    private final String username;
    @NotNull
    private final String password;
    private final int port;

    public MongoDBCredentials(final @JsonProperty("host") @NotNull String host, final @JsonProperty("username") @NotNull String username, final @JsonProperty("password") @NotNull String password, final @JsonProperty("port") int port) {
        this.host = host;
        this.username = username;
        this.password = password;
        this.port = port;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final @NotNull MongoDBCredentials that = (MongoDBCredentials) o;
        return this.port == that.port && Objects.equals(this.host, that.host) && Objects.equals(this.username, that.username) && Objects.equals(this.password, that.password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.host, this.username, this.password, this.port);
    }

    @Override
    public String toString() {
        return "MongoDBCredentials{" +
                "host='" + this.host + '\'' +
                ", username='" + this.username + '\'' +
                ", password='" + this.password + '\'' +
                ", port=" + this.port +
                '}';
    }

    @NotNull
    public String getHost() {
        return this.host;
    }

    @NotNull
    public String getUsername() {
        return this.username;
    }

    @NotNull
    public String getPassword() {
        return this.password;
    }

    public int getPort() {
        return port;
    }
}
