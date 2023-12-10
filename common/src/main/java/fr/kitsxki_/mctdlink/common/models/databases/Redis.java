package fr.kitsxki_.mctdlink.common.models.databases;

import fr.kitsxki_.mctdlink.common.impl.databases.models.credentials.RedisCredentials;
import fr.kitsxki_.mctdlink.common.models.MCTDLogger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.redisson.Redisson;
import org.redisson.api.RTopic;
import org.redisson.api.RedissonClient;
import org.redisson.api.listener.MessageListener;
import org.redisson.client.RedisConnectionException;
import org.redisson.config.Config;
import org.redisson.config.SingleServerConfig;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class Redis {

    @NotNull
    private final Map<String, RTopic> subscribedChannels = new HashMap<>();

    @NotNull
    private final MCTDLogger logger;
    @NotNull
    private final Config config;

    @Nullable
    private RedissonClient client;

    public Redis(final @NotNull RedisCredentials credentials) {
        final @NotNull Config config = new Config();
        final @NotNull SingleServerConfig serverConfig = config.useSingleServer();
        serverConfig.setAddress(String.format("redis://%s:%d", credentials.getHost(), credentials.getPort()));
        if(credentials.getUsername() != null)
            serverConfig.setUsername(credentials.getUsername());
        if(credentials.getPassword() != null)
            serverConfig.setUsername(credentials.getPassword());
        serverConfig.setDatabase(credentials.getDatabase());

        this.logger = new MCTDLogger("Redis");
        this.config = config;
    }

    public void initConnection() {
        new Thread(() -> this.client = Redisson.create(this.config), "Redis").start();
        this.logger.info("Successfully initialized Redis connection.");
    }

    public void closeConnection() {
        if(this.client == null)
            return;

        try {
            this.unsubscribeAll()
                    .thenAccept(voidObject -> {
                        this.client.shutdown();
                        this.client = null;
                        this.logger.info("Successfully closed Redis connection.");
                    })
                    .exceptionally(err -> {
                        this.logger.severe("An error occurred while unsubscribing to all channels!");
                        this.logger.severe(err.getMessage());
                        return null;
                    })
                    .get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    public <M> void publish(final @NotNull String channel, final @NotNull M message, final boolean failNotSubscribe) {
        if(failNotSubscribe && !this.subscribedChannels.containsKey(channel))
            throw new IllegalStateException(String.format("The %s channel is not subscribed yet! Set to false the \"failNotSubscribe\" parameter to avoid this error.", channel));
        else if(!this.subscribedChannels.containsKey(channel))
            this.publish(channel, message);

        final @NotNull RTopic topic = this.subscribedChannels.get(channel);
        topic.publishAsync(message)
                .thenAcceptAsync(receiversCount -> this.logger.info(String.format("Successfully published %s to %s!", message, channel)))
                .exceptionally(err -> {
                    this.logger.severe(String.format("An error occurred while publishing %s to %s!", message, channel));
                    this.logger.severe(err.getMessage());
                    return null;
                });
    }

    private <M> void publish(final @NotNull String channel, final @NotNull M message) {
        if(this.client == null)
            throw new RedisConnectionException("Redis connection is not initialized yet!");

        final @NotNull RTopic topic = this.client.getTopic(channel);
        topic.publishAsync(message)
                .thenAcceptAsync(receiversCount -> this.logger.info(String.format("Successfully published %s to %s!", message, channel)))
                .exceptionally(err -> {
                    this.logger.severe(String.format("An error occurred while publishing %s to %s!", message, channel));
                    this.logger.severe(err.getMessage());
                    return null;
                });
    }

    public <M> void subscribe(final @NotNull String channel, final @NotNull Class<M> clazz, final @NotNull MessageListener<M> messageListener) {
        if(this.client == null)
            throw new RedisConnectionException("Redis connection is not initialized yet!");

        final @NotNull RTopic topic = this.client.getTopic(channel);
        topic.addListenerAsync(clazz, messageListener)
                .thenAcceptAsync(topicId -> {
                    this.subscribedChannels.put(channel, topic);
                    this.logger.info(String.format("Successfully subscribed to %s!", channel));
                })
                .exceptionally(err -> {
                    this.logger.severe(String.format("An error occurred while subscribing to %s!", channel));
                    this.logger.severe(err.getMessage());
                    return null;
                });
    }

    public CompletableFuture<Void> unsubscribe(final @NotNull String channel) {
        return CompletableFuture.supplyAsync(() -> {
            if(!this.subscribedChannels.containsKey(channel))
                throw new NullPointerException(String.format("The %s channel is not subscribed yet, cannot unsubscribe it!", channel));

            this.subscribedChannels.remove(channel).removeAllListenersAsync()
                    .thenAcceptAsync(voidObject -> this.logger.info(String.format("Successfully unsubscribed to %s!", channel)))
                    .exceptionally(err -> {
                        this.logger.severe(String.format("An error occurred while unsubscribing to %s!", channel));
                        this.logger.severe(err.getMessage());
                        return null;
                    });

            return null;
        });
    }

    public CompletableFuture<Void> unsubscribeAll() {
        return CompletableFuture.supplyAsync(() -> {
            this.subscribedChannels.forEach((n, t) -> {
                try {
                    t.removeAllListenersAsync().get();
                    this.logger.info(String.format("Successfully unsubscribed to %s!", n));
                } catch (InterruptedException | ExecutionException e) {
                    throw new RuntimeException(e);
                }
            });
            this.logger.info("Successfully unsubscribed to all channels!");
            return null;
        });
    }

    @Nullable
    public RedissonClient getClient() {
        return this.client;
    }
}