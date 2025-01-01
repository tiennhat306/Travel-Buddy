package com.travelbuddy.persistence.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "sites")
@ToString(exclude = {"userEntity", "travelPlanEntities", "siteVersionEntities", "siteReviewEntities", "siteReactions", "siteMedias", "siteReports"})
public class SiteEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "owner_id", nullable = false)
    private Integer ownerId;

    @Column(name = "enabled", nullable = false)
    @Builder.Default
    private boolean enabled = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", insertable = false, updatable = false)
    private UserEntity userEntity;

    @ManyToMany(mappedBy = "siteEntities", fetch = FetchType.LAZY)
    private List<TravelPlanEntity> travelPlanEntities;

    @OneToMany(mappedBy = "siteEntity", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SiteVersionEntity> siteVersionEntities;

    @OneToMany(mappedBy = "siteEntity", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SiteReviewEntity> siteReviewEntities;

    @OneToMany(mappedBy = "siteEntity", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SiteReactionEntity> siteReactions;

    @OneToMany(mappedBy = "site", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SiteMediaEntity> siteMedias;

    @OneToMany(mappedBy = "siteEntity", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SiteReportEntity> siteReports;

    @PrePersist
    public void prePersist() {
        this.enabled = true;
    }
}