package com.gestionevent.eventservice.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {

    @Value("${kafka.topics.event-created}")
    private String eventCreatedTopic;

    @Bean
    public NewTopic eventCreatedTopic() {
        return TopicBuilder.name(eventCreatedTopic)
                .partitions(3)
                .replicas(1)
                .build();
    }
}
