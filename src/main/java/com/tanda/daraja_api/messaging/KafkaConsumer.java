package com.tanda.daraja_api.messaging;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class KafkaConsumer {

    @KafkaListener(topics = "events.all", groupId = "my-consumer-group")
    public void consume(String message) {
    }
}