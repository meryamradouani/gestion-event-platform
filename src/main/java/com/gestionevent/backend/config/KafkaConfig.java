package com.gestionevent.backend.config;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableKafka
public class KafkaConfig {
    
    @Value("${spring.kafka.bootstrap-servers:localhost:9092}")
    private String bootstrapServers;
    
    /**
     * Factory pour les événements de type UserRegisteredEvent (registrations.created)
     */
    @Bean
    public ConsumerFactory<String, Object> registrationConsumerFactory() {
        Map<String, Object> props = getBaseConsumerProps();
        props.put(JsonDeserializer.VALUE_DEFAULT_TYPE, "com.gestionevent.backend.event.UserRegisteredEvent");
        return new DefaultKafkaConsumerFactory<>(props);
    }
    
    /**
     * Factory pour les événements de type UserTokenUpdatedEvent (user.tokens.updated)
     */
    @Bean
    public ConsumerFactory<String, Object> tokenConsumerFactory() {
        Map<String, Object> props = getBaseConsumerProps();
        props.put(JsonDeserializer.VALUE_DEFAULT_TYPE, "com.gestionevent.backend.event.UserTokenUpdatedEvent");
        return new DefaultKafkaConsumerFactory<>(props);
    }
    
    /**
     * Factory pour les événements de type EventCreatedEvent (events.created)
     */
    @Bean
    public ConsumerFactory<String, Object> eventConsumerFactory() {
        Map<String, Object> props = getBaseConsumerProps();
        props.put(JsonDeserializer.VALUE_DEFAULT_TYPE, "com.gestionevent.backend.event.EventCreatedEvent");
        return new DefaultKafkaConsumerFactory<>(props);
    }
    
    /**
     * Configuration de base pour tous les consumers
     */
    private Map<String, Object> getBaseConsumerProps() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "notification-service");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        
        // Configuration de la désérialisation avec ErrorHandlingDeserializer
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
        
        // Configuration du délégué pour ErrorHandlingDeserializer
        props.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, JsonDeserializer.class.getName());
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
        
        // Gestion des erreurs de désérialisation
        props.put(ErrorHandlingDeserializer.KEY_DESERIALIZER_CLASS, StringDeserializer.class);
        
        return props;
    }
    
    /**
     * Factory par défaut (pour compatibilité)
     */
    @Bean
    public ConsumerFactory<String, Object> consumerFactory() {
        return registrationConsumerFactory();
    }
    
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Object> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, Object> factory = 
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        factory.setConcurrency(3); // 3 threads pour consommer en parallèle
        
        // Gestion des erreurs au niveau du listener
        factory.setCommonErrorHandler(new org.springframework.kafka.listener.DefaultErrorHandler());
        
        return factory;
    }
    
    /**
     * Factory spécifique pour registrations.created
     */
    @Bean("registrationListenerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, Object> registrationListenerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, Object> factory = 
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(registrationConsumerFactory());
        factory.setConcurrency(1);
        factory.setCommonErrorHandler(new org.springframework.kafka.listener.DefaultErrorHandler());
        return factory;
    }
    
    /**
     * Factory spécifique pour user.tokens.updated
     */
    @Bean("tokenListenerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, Object> tokenListenerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, Object> factory = 
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(tokenConsumerFactory());
        factory.setConcurrency(1);
        factory.setCommonErrorHandler(new org.springframework.kafka.listener.DefaultErrorHandler());
        return factory;
    }
    
    /**
     * Factory spécifique pour events.created
     */
    @Bean("eventListenerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, Object> eventListenerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, Object> factory = 
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(eventConsumerFactory());
        factory.setConcurrency(1);
        factory.setCommonErrorHandler(new org.springframework.kafka.listener.DefaultErrorHandler());
        return factory;
    }
}