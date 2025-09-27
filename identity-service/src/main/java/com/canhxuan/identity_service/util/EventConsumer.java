package com.canhxuan.identity_service.util;

import com.canhxuan.identity_service.dto.EmailDTO;
import com.canhxuan.identity_service.entity.Identity;
import com.canhxuan.identity_service.repository.IdentityRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import dto.UserCreatedEvent;
import dto.UserUpdatedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class EventConsumer {
    @Autowired
    private final IdentityRepository identityRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JavaMailSender mailSender;

    public EventConsumer(IdentityRepository identityRepository) {
        this.identityRepository = identityRepository;
    }

    @KafkaListener(topics = "email.topic", groupId = "identity-service")
    public void handleEmailMessage(String message) {
        log.info("Received email message: {}", message);
        try {
            ObjectMapper mapper = new ObjectMapper();
            EmailDTO emailDTO = mapper.readValue(message, EmailDTO.class);
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setFrom("vhoang1114@gmail.com");
            mailMessage.setTo(emailDTO.getTo());
            mailMessage.setSubject(emailDTO.getSubject());
            mailMessage.setText(emailDTO.getContent());
            mailSender.send(mailMessage);
            System.out.println("Processed email message: " + message);
        } catch (Exception e) {
            log.error("Error processing email message", e);
            throw new RuntimeException(e);
        }
    }

    @RetryableTopic(backoff = @Backoff(delay = 2000, multiplier = 2), dltTopicSuffix = "-dlt")
    @KafkaListener(topics = "user.created", groupId = "identity-service")
    public void handleUserCreated(UserCreatedEvent event) {
        log.info("Received user created event: {}", event);
        Identity identity = new Identity();
        identity.setId(event.getUserId());
        identity.setUsername(event.getUsername());
        identity.setPassword(passwordEncoder.encode(event.getPassword()));
        identity.setRole(event.getRole());
        identityRepository.save(identity);
        System.out.println("Processed event: " + event);
    }

    @RetryableTopic(backoff = @Backoff(delay = 2000, multiplier = 2), dltTopicSuffix = "-dlt")
    @KafkaListener(topics = "user.updated", groupId = "identity-service")
    public void handleUserUpdated(UserUpdatedEvent event) {
        log.info("Received user updated event: {}", event);
        Identity identity = new Identity();
        identity.setId(event.getUserId());
        identity.setUsername(event.getUsername());
        identity.setPassword(passwordEncoder.encode(event.getPassword()));
        identity.setRole(event.getRole());
        identityRepository.save(identity);
        System.out.println("Processed event: " + event);
    }
}


