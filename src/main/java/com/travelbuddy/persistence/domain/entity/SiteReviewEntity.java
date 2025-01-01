package com.travelbuddy.persistence.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "site_reviews")
@ToString(exclude = {"siteEntity", "userEntity", "reviewMedias", "reviewReactions", "reports"})
public class SiteReviewEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "site_id")
    private Integer siteId;

    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "comment")
    private String comment;

    @Column(name = "general_rating")
    private Integer generalRating;

    @Column(name = "arrival_date")
    private LocalDate arrivalDate;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "enabled", nullable = false)
    @Builder.Default
    private boolean enabled = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "site_id", insertable = false, updatable = false)
    private SiteEntity siteEntity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private UserEntity userEntity;

    @OneToMany(mappedBy = "review", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReviewMediaEntity> reviewMedias;

    @OneToMany(mappedBy = "siteReviewEntity", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReviewReactionEntity> reviewReactions;

    @OneToMany(mappedBy = "siteReviewEntity", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SiteReviewReportEntity> reports;

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
        enabled = true;
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
