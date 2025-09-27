package com.canhxuan.identity_service.service;

import com.canhxuan.identity_service.dto.CustomUserDetails;
import com.canhxuan.identity_service.entity.Identity;
import com.canhxuan.identity_service.repository.IdentityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomUserDetailService implements UserDetailsService {

    @Autowired
    private IdentityRepository identityRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Identity identity = identityRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
        return new CustomUserDetails(
                identity.getId(),
                identity.getUsername(),
                identity.getPassword(),
                List.of(new SimpleGrantedAuthority("ROLE_" + identity.getRole()))
        );
    }
}
