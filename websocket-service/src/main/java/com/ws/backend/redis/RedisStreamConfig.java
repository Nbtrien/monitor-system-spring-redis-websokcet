package com.ws.backend.redis;

import com.ws.backend.redis.RedisStreamListener;
import io.lettuce.core.RedisBusyException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.RedisSystemException;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.stream.Consumer;
import org.springframework.data.redis.connection.stream.ObjectRecord;
import org.springframework.data.redis.connection.stream.ReadOffset;
import org.springframework.data.redis.connection.stream.StreamOffset;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.stream.StreamMessageListenerContainer;
import org.springframework.data.redis.stream.Subscription;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.Duration;

@Configuration
@RequiredArgsConstructor
@Log4j2
public class RedisStreamConfig {
    private final RedisStreamListener redisStreamListener;
    private final RedisConnectionFactory connectionFactory;
    private final RedisTemplate<String, String> redisTemplate;
    @Value("${redis.stream.metrics.key}")
    private String streamKey;


    @Bean
    public Subscription subscription() throws UnknownHostException {
//        StreamOffset<String> streamOffset = StreamOffset.create(streamKey, ReadOffset.latest());
        String uniqueConsumerGroup = streamKey + "-" + InetAddress.getLocalHost().getHostName();

        createConsumerGroupIfNotExists(connectionFactory, streamKey, uniqueConsumerGroup);

        StreamOffset<String> streamOffset = StreamOffset.create(streamKey, ReadOffset.lastConsumed());


        StreamMessageListenerContainer.StreamMessageListenerContainerOptions<String, ObjectRecord<String, String>> options = StreamMessageListenerContainer.StreamMessageListenerContainerOptions.builder()
                .pollTimeout(Duration.ofMillis(100)).targetType(String.class).build();

        StreamMessageListenerContainer<String, ObjectRecord<String, String>> container =
                StreamMessageListenerContainer.create(connectionFactory, options);

        Subscription subscription = container.receiveAutoAck(Consumer.from(streamKey, uniqueConsumerGroup),
                                                             StreamOffset.create(streamKey,
                                                                                 ReadOffset.lastConsumed()),
                                                             redisStreamListener);
        container.start();
        return subscription;
    }

    private void createConsumerGroupIfNotExists(RedisConnectionFactory redisConnectionFactory, String streamKey,
                                                String groupName) {
        try {
            log.info("Creating group ....");
            redisConnectionFactory.getConnection().streamCommands()
                    .xGroupCreate(streamKey.getBytes(), groupName, ReadOffset.latest(), true);
            log.info("Create group");
        } catch (RedisSystemException exception) {
            if (exception.getCause() instanceof RedisBusyException) {
                log.info("Consumer group '{}' already exists", groupName);
            } else {
//                log.error("Error creating consumer group: {}", exception.getMessage());
                exception.printStackTrace();
            }
        }
    }
}
