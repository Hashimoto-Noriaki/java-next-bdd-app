package com.example.matching.presentation;

import com.example.matching.application.MatchingService;
import com.example.matching.presentation.dto.CandidateResponse;
import com.example.matching.presentation.dto.LikeResponse;
import com.example.matching.presentation.dto.MatchResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

@RestController
public class MatchingController {

    private final MatchingService matchingService;

    public MatchingController(MatchingService matchingService) {
        this.matchingService = matchingService;
    }

    @PostMapping("/likes/{targetUserId}")
    public ResponseEntity<LikeResponse> sendLike(
            @PathVariable Long targetUserId,
            Principal principal) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(matchingService.sendLike(principal.getName(), targetUserId));
    }

    @GetMapping("/matches")
    public ResponseEntity<List<MatchResponse>> getMatches(Principal principal) {
        return ResponseEntity.ok(matchingService.getMatches(principal.getName()));
    }

    @GetMapping("/candidates")
    public ResponseEntity<List<CandidateResponse>> getCandidates(Principal principal) {
        return ResponseEntity.ok(matchingService.getCandidates(principal.getName()));
    }
}
