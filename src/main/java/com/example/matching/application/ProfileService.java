package com.example.matching.application;

import com.example.matching.domain.Profile;
import com.example.matching.domain.User;
import com.example.matching.domain.exception.DuplicateProfileException;
import com.example.matching.domain.exception.ProfileNotFoundException;
import com.example.matching.domain.exception.UserNotFoundException;
import com.example.matching.infrastructure.ProfileRepository;
import com.example.matching.infrastructure.UserRepository;
import com.example.matching.presentation.dto.CreateProfileRequest;
import com.example.matching.presentation.dto.ProfileResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ProfileService {

    private final ProfileRepository profileRepository;
    private final UserRepository userRepository;

    public ProfileService(ProfileRepository profileRepository, UserRepository userRepository) {
        this.profileRepository = profileRepository;
        this.userRepository = userRepository;
    }

    public ProfileResponse create(String email, CreateProfileRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(email));

        if (profileRepository.existsByUser(user)) {
            throw new DuplicateProfileException();
        }

        Profile profile = new Profile(user, request.gender(), request.age(), request.prefecture());
        profile.setOccupation(request.occupation());
        profile.setIncome(request.income());
        profile.setHeight(request.height());
        profile.setEducation(request.education());
        profile.setBodyType(request.bodyType());
        profile.setSelfIntroduction(request.selfIntroduction());
        profile.setHobbies(request.hobbies());
        profile.setLifestyle(request.lifestyle());
        profile.setRelationshipHistory(request.relationshipHistory());

        Profile saved = profileRepository.save(profile);
        return toResponse(saved);
    }

    public ProfileResponse update(String email, CreateProfileRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(email));
        Profile profile = profileRepository.findByUser(user)
                .orElseThrow(() -> new ProfileNotFoundException("プロフィールが見つかりません"));

        profile.setGender(request.gender());
        profile.setAge(request.age());
        profile.setPrefecture(request.prefecture());
        profile.setOccupation(request.occupation());
        profile.setIncome(request.income());
        profile.setHeight(request.height());
        profile.setEducation(request.education());
        profile.setBodyType(request.bodyType());
        profile.setSelfIntroduction(request.selfIntroduction());
        profile.setHobbies(request.hobbies());
        profile.setLifestyle(request.lifestyle());
        profile.setRelationshipHistory(request.relationshipHistory());

        return toResponse(profile);
    }

    @Transactional(readOnly = true)
    public ProfileResponse getMyProfile(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(email));
        Profile profile = profileRepository.findByUser(user)
                .orElseThrow(() -> new ProfileNotFoundException("プロフィールが見つかりません"));
        return toResponse(profile);
    }

    @Transactional(readOnly = true)
    public ProfileResponse getProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
        Profile profile = profileRepository.findByUser(user)
                .orElseThrow(() -> new ProfileNotFoundException("プロフィールが見つかりません"));
        return toResponse(profile);
    }

    private ProfileResponse toResponse(Profile profile) {
        return new ProfileResponse(
                profile.getId(),
                profile.getUser().getId(),
                profile.getGender().name(),
                profile.getAge(),
                profile.getPrefecture(),
                profile.getOccupation(),
                profile.getIncome(),
                profile.getHeight(),
                profile.getEducation(),
                profile.getBodyType(),
                profile.getSelfIntroduction(),
                profile.getHobbies(),
                profile.getLifestyle(),
                profile.getRelationshipHistory()
        );
    }
}
