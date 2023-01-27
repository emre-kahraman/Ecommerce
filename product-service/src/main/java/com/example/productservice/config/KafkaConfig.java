package com.example.productservice.config;

import com.example.productservice.dto.AddItemToCartRequest;
import com.example.productservice.dto.DeleteCartItemRequest;
import com.example.productservice.dto.UpdateCartItemRequest;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String kafka;

    @Value("${spring.kafka.topic}")
    private String productTopic;

    @Value("${spring.kafka.topic2}")
    private String updatedProductTopic;

    @Value("${spring.kafka.topic3}")
    private String deletedProductTopic;

    @Bean
    public NewTopic productTopic(){
        return TopicBuilder.name(productTopic)
                .partitions(1)
                .replicas(1).build();
    }

    @Bean
    public NewTopic updatedProductTopic(){
        return TopicBuilder.name(updatedProductTopic)
                .partitions(1)
                .replicas(1).build();
    }

    @Bean
    public NewTopic deletedProductTopic(){
        return TopicBuilder.name(deletedProductTopic)
                .partitions(1)
                .replicas(1).build();
    }

    @Bean
    public ProducerFactory<String, AddItemToCartRequest> productFactory() {
        return new DefaultKafkaProducerFactory<>(producerConfigs());
    }

    @Bean
    public ProducerFactory<String, UpdateCartItemRequest> updatedProductFactory() {
        return new DefaultKafkaProducerFactory<>(producerConfigs());
    }

    @Bean
    public ProducerFactory<String, DeleteCartItemRequest> deletedProductFactory() {
        return new DefaultKafkaProducerFactory<>(producerConfigs());
    }

    @Bean
    public Map<String, Object> producerConfigs() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafka);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return props;
    }

    @Bean
    public KafkaTemplate<String, AddItemToCartRequest> productTemplate() {
        return new KafkaTemplate<String, AddItemToCartRequest>(productFactory());
    }

    @Bean
    public KafkaTemplate<String, UpdateCartItemRequest> updatedProductTemplate() {
        return new KafkaTemplate<String, UpdateCartItemRequest>(updatedProductFactory());
    }

    @Bean
    public KafkaTemplate<String, DeleteCartItemRequest> deletedProductTemplate() {
        return new KafkaTemplate<String, DeleteCartItemRequest>(deletedProductFactory());
    }
}
