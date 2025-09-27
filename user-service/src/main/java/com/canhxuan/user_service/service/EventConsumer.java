package com.canhxuan.user_service.service;

import com.canhxuan.user_service.entity.UserProfile;
import com.canhxuan.user_service.repository.UserRepository;
import dto.RegisterEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class EventConsumer {

    @Autowired
    private UserRepository userRepository;

    @KafkaListener(topics = "identity.created", groupId = "identity-service", containerFactory = "registerEventKafkaListenerContainerFactory")
    public void handleRegisterEvent(RegisterEvent registerEvent) {
        log.info("Received register event: {}", registerEvent);
        UserProfile user = new UserProfile();
        user.setId(registerEvent.getIdentityId());
        user.setEmail(registerEvent.getEmail());
        userRepository.save(user);
        log.info("Đã tạo user mới với ID: {}", registerEvent.getIdentityId());
    }
}
