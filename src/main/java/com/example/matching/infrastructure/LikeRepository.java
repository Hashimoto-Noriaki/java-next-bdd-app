package com.example.matching.infrastructure;

import com.example.matching.domain.Like;
import com.example.matching.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LikeRepository extends JpaRepository<Like, Long> {
    boolean existsBySenderAndReceiver(User sender, User receiver);
    List<Like> findBySender(User sender);
}
