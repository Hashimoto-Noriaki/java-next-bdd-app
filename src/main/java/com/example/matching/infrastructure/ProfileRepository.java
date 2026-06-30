package com.example.matching.infrastructure;

import com.example.matching.domain.Profile;
import com.example.matching.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProfileRepository extends JpaRepository<Profile, Long> {
    Optional<Profile> findByUser(User user);
    boolean existsByUser(User user);
}
