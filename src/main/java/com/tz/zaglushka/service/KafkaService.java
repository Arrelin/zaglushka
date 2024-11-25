package com.tz.zaglushka.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.tz.zaglushka.producer.KafkaProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaService {

    private final KafkaProducer kafkaProducer;
    private final ObjectMapper objectMapper;



    @KafkaListener(topics = "topic1", groupId = "consumer_1")
    public void consume(ConsumerRecord<String, String> record) {
        String value = record.value();
        try {
            String updatedValue = updateMessage(value);
            kafkaProducer.sendMessage(record.key(), updatedValue);
        } catch (IOException e) {
            log.error("Problem when sending message to topic2", e);
        }
    }

    private String updateMessage(String value) throws IOException {
        JsonNode jsonNode = objectMapper.readTree(value);
        if (jsonNode.has("id")) {
            String id = jsonNode.get("id").asText();
            ((ObjectNode) jsonNode).put("id", id + "123");
            log.info("Modifying id with magical 123. New ID: {}", id + "123");
            return objectMapper.writeValueAsString(jsonNode);
        } else {
            log.error("The JSON message does not contain an 'id' field: {}", value);
            throw new IOException("Missing 'id' field in JSON message");
        }
    }
}