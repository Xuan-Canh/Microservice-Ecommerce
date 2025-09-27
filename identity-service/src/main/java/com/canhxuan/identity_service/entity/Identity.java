package com.canhxuan.identity_service.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Entity
@Data
@Table(name = "identity")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Identity {
    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(unique = true, nullable = false)
    String username;

    @Column(nullable = false)
    String password;

    String email;
    String role = "USER";
    boolean isVerifiedEmail = false;
    int status = 1;
}
