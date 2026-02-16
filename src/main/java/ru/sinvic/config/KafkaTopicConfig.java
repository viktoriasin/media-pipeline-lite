package ru.sinvic.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    public static final String ANALYTICS_EVENTS_TOPIC = "playback-events";

    @Bean
    public NewTopic analyticsEventsTopic() {
        return TopicBuilder
            .name(ANALYTICS_EVENTS_TOPIC)
            .partitions(3)
            .replicas(1)
            .build();
    }
}
