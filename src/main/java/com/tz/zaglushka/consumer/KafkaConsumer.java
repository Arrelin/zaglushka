package com.tz.zaglushka.consumer;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class KafkaConsumer {

    @KafkaListener(topics = "topic1", groupId = "consumer_1")
    public void consume(ConsumerRecord<String, String> record) {
        String key = record.key();
        String value = record.value();
        log.info("Consumed message with key: {}, value: {}", key, value);
    }
}
