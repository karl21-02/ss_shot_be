package com.ss_shot.ss_shot_be.repository;

import com.ss_shot.ss_shot_be.entity.AuthProvider;
import com.ss_shot.ss_shot_be.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    Optional<User> findByProviderAndProviderId(AuthProvider provider, String providerId);

    boolean existsByProviderAndProviderId(AuthProvider provider, String providerId);
}
