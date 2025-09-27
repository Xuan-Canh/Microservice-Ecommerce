package com.canhxuan.user_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Service
@FeignClient(name = "identity-service", url = "http://localhost:8083/canhxuan/auth")
public interface IdentityClientService {
    @GetMapping("/check-username")
    Boolean checkUsernameExists(@RequestParam("username") String username);
}
