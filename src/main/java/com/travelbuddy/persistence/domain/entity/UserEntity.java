package com.travelbuddy.persistence.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
@ToString(exclude = {"siteReviews", "reviewReactions", "travelPlans", "siteEntities", "siteReactions", "reports"})
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String email;

    private String password;

    private String nickname;

    @Column(name = "enabled")
    private boolean enabled = false;

    private String fullName;

    private String gender;

    private String phoneNumber;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    private String address;

    @Column(name = "social_url")
    private String socialUrl;

    private Double score;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JoinColumn(name = "avatar_id", referencedColumnName = "id")
    private FileEntity avatar;

    @OneToMany(mappedBy = "userEntity", fetch = FetchType.LAZY)
    private List<SiteEntity> siteEntities;

    @OneToMany(mappedBy = "userEntity", fetch = FetchType.LAZY)
    private List<SiteReactionEntity> siteReactions;

    @OneToMany(mappedBy = "userEntity", fetch = FetchType.LAZY)
    private List<SiteReviewEntity> siteReviews;

    @OneToMany(mappedBy = "userEntity", fetch = FetchType.LAZY)
    private List<ReviewReactionEntity> reviewReactions;

    @ManyToMany(mappedBy = "userEntities", fetch = FetchType.LAZY)
    private List<TravelPlanEntity> travelPlans;

    @OneToMany(mappedBy = "userEntity", fetch = FetchType.LAZY)
    private List<UserReportEntity> reports;

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }
}