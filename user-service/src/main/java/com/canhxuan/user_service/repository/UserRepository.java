package com.canhxuan.user_service.repository;

import com.canhxuan.user_service.entity.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserRepository  extends JpaRepository<UserProfile, Long> {
}
