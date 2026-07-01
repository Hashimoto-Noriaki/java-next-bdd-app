package com.example.matching.infrastructure;

import com.example.matching.domain.Match;
import com.example.matching.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MatchRepository extends JpaRepository<Match, Long> {
    List<Match> findByUser1OrUser2(User user1, User user2);
}
