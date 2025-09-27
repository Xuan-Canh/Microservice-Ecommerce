package com.canhxuan.identity_service.repository;

import com.canhxuan.identity_service.entity.Identity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IdentityRepository extends JpaRepository<Identity, Long> {
    Optional<Identity> findByUsername(String username);
    Optional<Identity> findByEmail(String email);
}
