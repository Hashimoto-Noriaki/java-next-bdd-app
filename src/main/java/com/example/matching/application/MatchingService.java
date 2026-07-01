package com.example.matching.application;

import com.example.matching.domain.Like;
import com.example.matching.domain.Match;
import com.example.matching.domain.User;
import com.example.matching.domain.exception.UserNotFoundException;
import com.example.matching.infrastructure.LikeRepository;
import com.example.matching.infrastructure.MatchRepository;
import com.example.matching.infrastructure.UserRepository;
import com.example.matching.presentation.dto.CandidateResponse;
import com.example.matching.presentation.dto.LikeResponse;
import com.example.matching.presentation.dto.MatchResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Transactional
public class MatchingService {

    private final UserRepository userRepository;
    private final LikeRepository likeRepository;
    private final MatchRepository matchRepository;

    public MatchingService(UserRepository userRepository,
                           LikeRepository likeRepository,
                           MatchRepository matchRepository) {
        this.userRepository = userRepository;
        this.likeRepository = likeRepository;
        this.matchRepository = matchRepository;
    }

    public LikeResponse sendLike(String email, Long targetUserId) {
        User sender = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(email));
        User receiver = userRepository.findById(targetUserId)
                .orElseThrow(() -> new UserNotFoundException(targetUserId));

        likeRepository.save(new Like(sender, receiver));

        boolean matched = likeRepository.existsBySenderAndReceiver(receiver, sender);
        if (matched) {
            matchRepository.save(new Match(sender, receiver));
        }

        return new LikeResponse(matched);
    }

    @Transactional(readOnly = true)
    public List<MatchResponse> getMatches(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(email));
        return matchRepository.findByUser1OrUser2(user, user).stream()
                .map(m -> new MatchResponse(m.getId(), m.getOther(user).getId(), m.getOther(user).getName()))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<CandidateResponse> getCandidates(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(email));

        Set<Long> excludeIds = new HashSet<>();
        excludeIds.add(user.getId());
        likeRepository.findBySender(user).forEach(l -> excludeIds.add(l.getReceiver().getId()));

        return userRepository.findAll().stream()
                .filter(u -> !excludeIds.contains(u.getId()))
                .map(u -> new CandidateResponse(u.getId(), u.getName()))
                .toList();
    }
}
