package com.example.matching.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "matches",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user1_id", "user2_id"}),
        indexes = {
                @Index(name = "idx_match_user1", columnList = "user1_id"),
                @Index(name = "idx_match_user2", columnList = "user2_id")
        })
public class Match {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user1_id", nullable = false)
    private User user1;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user2_id", nullable = false)
    private User user2;

    protected Match() {}

    public Match(User user1, User user2) {
        this.user1 = user1;
        this.user2 = user2;
    }

    public Long getId() { return id; }
    public User getUser1() { return user1; }
    public User getUser2() { return user2; }

    public User getOther(User user) {
        return user1.getId().equals(user.getId()) ? user2 : user1;
    }
}
