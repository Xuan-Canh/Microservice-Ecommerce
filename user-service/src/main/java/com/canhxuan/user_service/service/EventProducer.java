package com.canhxuan.user_service.service;

import com.canhxuan.user_service.dto.CreateUserRequest;
import com.canhxuan.user_service.dto.UpdateUserRequest;
import dto.UserCreatedEvent;
import dto.UserUpdatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventProducer {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    private static final String USER_CREATED_TOPIC = "user.created";
    private static final String USER_UPDATED_TOPIC = "user.updated";

    @Async
    public CompletableFuture<Void> publishUserCreated(CreateUserRequest request) {
        return CompletableFuture.runAsync(() -> {
            try {
                UserCreatedEvent userCreatedEvent = new UserCreatedEvent(
                        request.getId(),
                        request.getUsername(),
                        request.getPassword(),
                        request.getEmail(),
                        request.getRole(),
                        request.getStatus()
                );
                kafkaTemplate.send(USER_CREATED_TOPIC, request.getId().toString() , userCreatedEvent);
                log.info("Published user created event for user: {}", request.getId());
            } catch (Exception e) {
                log.error("Error publishing user created event", e);
            }
        });
    }

    @Async
    public CompletableFuture<Void> publishUserUpdated(UpdateUserRequest request) {
        return CompletableFuture.runAsync(() -> {
            try {
                UserUpdatedEvent userUpdatedEvent = new UserUpdatedEvent(
                        request.getId(),
                        request.getUsername(),
                        request.getPassword(),
                        request.getEmail(),
                        request.getRole(),
                        request.getStatus()
                );
                kafkaTemplate.send(USER_UPDATED_TOPIC, request.getId().toString() , userUpdatedEvent);
                log.info("Published request updated event for request: {}", request.getId());
            } catch (Exception e) {
                log.error("Error publishing request updated event", e);
            }
        });
    }
}
