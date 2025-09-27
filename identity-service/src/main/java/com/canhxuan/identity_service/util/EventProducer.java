package com.canhxuan.identity_service.util;

import com.canhxuan.identity_service.entity.Identity;
import dto.RegisterEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Component
@Slf4j
public class EventProducer {
    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    public void sendEmailMessage(String emailMessage) {
        kafkaTemplate.send("email.topic", emailMessage);
        log.info("Sent email message successfully: {}", emailMessage);
    }

    @Async
    public CompletableFuture<Void> publishRegisterEvent(Identity identity) {
        return CompletableFuture.runAsync(() -> {
            try {
                RegisterEvent registerEvent = new RegisterEvent(
                        identity.getId(),
                        identity.getEmail()
                );
                kafkaTemplate.send("identity.created", identity.getId().toString(), registerEvent);
                log.info("Published register event for identity: {}", identity.getId());
            } catch (Exception e) {
                log.error("Error publishing register event", e);
            }
        });
    }
}
