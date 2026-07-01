package com.example.matching.presentation;

import com.example.matching.application.ProfileService;
import com.example.matching.presentation.dto.CreateProfileRequest;
import com.example.matching.presentation.dto.ProfileResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/profile")
public class ProfileController {

    private final ProfileService profileService;

    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }

    @PostMapping
    public ResponseEntity<ProfileResponse> create(
            Principal principal,
            @RequestBody @Valid CreateProfileRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(profileService.create(principal.getName(), request));
    }

    @PutMapping
    public ResponseEntity<ProfileResponse> update(
            Principal principal,
            @RequestBody @Valid CreateProfileRequest request) {
        return ResponseEntity.ok(profileService.update(principal.getName(), request));
    }

    @GetMapping("/me")
    public ResponseEntity<ProfileResponse> getMyProfile(Principal principal) {
        return ResponseEntity.ok(profileService.getMyProfile(principal.getName()));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<ProfileResponse> getProfile(@PathVariable Long userId) {
        return ResponseEntity.ok(profileService.getProfile(userId));
    }
}
