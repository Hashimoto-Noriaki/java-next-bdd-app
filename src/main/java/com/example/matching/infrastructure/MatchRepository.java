package com.example.matching.infrastructure;

import com.example.matching.domain.Match;
import com.example.matching.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MatchRepository extends JpaRepository<Match, Long> {
    List<Match> findByUser1OrUser2(User user1, User user2);
    boolean existsByUser1AndUser2OrUser2AndUser1(User user1a, User user2a, User user1b, User user2b);
}
