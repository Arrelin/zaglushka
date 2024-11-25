package com.tz.zaglushka.producer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class KafkaProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;

    @Value("${spring.kafka.template.default-topic}")
    private String outputTopic;

    public KafkaProducer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendMessage(String key, String message) {
        log.info("trying to send message");
        kafkaTemplate.send(outputTopic, key, message);
        log.info("success sending message");
    }
}