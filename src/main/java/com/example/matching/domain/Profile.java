package com.example.matching.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "profiles")
public class Profile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Column(nullable = false)
    private int age;

    @Column(nullable = false)
    private String prefecture;

    private String occupation;
    private Integer income;
    private Integer height;
    private String education;
    private String bodyType;

    @Column(length = 500)
    private String selfIntroduction;

    private String hobbies;
    private String lifestyle;
    private String relationshipHistory;

    protected Profile() {}

    public Profile(User user, Gender gender, int age, String prefecture) {
        this.user = user;
        this.gender = gender;
        this.age = age;
        this.prefecture = prefecture;
    }

    public Long getId() { return id; }
    public User getUser() { return user; }
    public Gender getGender() { return gender; }
    public int getAge() { return age; }
    public String getPrefecture() { return prefecture; }
    public String getOccupation() { return occupation; }
    public Integer getIncome() { return income; }
    public Integer getHeight() { return height; }
    public String getEducation() { return education; }
    public String getBodyType() { return bodyType; }
    public String getSelfIntroduction() { return selfIntroduction; }
    public String getHobbies() { return hobbies; }
    public String getLifestyle() { return lifestyle; }
    public String getRelationshipHistory() { return relationshipHistory; }

    public void setOccupation(String occupation) { this.occupation = occupation; }
    public void setIncome(Integer income) { this.income = income; }
    public void setHeight(Integer height) { this.height = height; }
    public void setEducation(String education) { this.education = education; }
    public void setBodyType(String bodyType) { this.bodyType = bodyType; }
    public void setSelfIntroduction(String selfIntroduction) { this.selfIntroduction = selfIntroduction; }
    public void setHobbies(String hobbies) { this.hobbies = hobbies; }
    public void setLifestyle(String lifestyle) { this.lifestyle = lifestyle; }
    public void setRelationshipHistory(String relationshipHistory) { this.relationshipHistory = relationshipHistory; }
    public void setGender(Gender gender) { this.gender = gender; }
    public void setAge(int age) { this.age = age; }
    public void setPrefecture(String prefecture) { this.prefecture = prefecture; }
}
