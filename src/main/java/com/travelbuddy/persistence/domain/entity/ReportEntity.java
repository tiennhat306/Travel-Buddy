package com.travelbuddy.persistence.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Builder
@Entity
@Table(name = "reports")
@ToString(exclude = {"reportCategoryEntity", "user", "siteReportEntity"})
public class ReportEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "category_id", nullable = false)
    private Integer categoryId;

    private String description;

    @Column(name = "reported_by", insertable = false, updatable = false)
    private Integer reportedBy;

    private LocalDateTime createdAt;

    // many to one with report sub category
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", referencedColumnName = "id", insertable = false, updatable = false)
    private ReportCategoryEntity reportCategoryEntity;

    // many to one with user
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reported_by", referencedColumnName = "id")
    private UserEntity user;

    // one to one with site report
    @OneToOne(mappedBy = "reportEntity", fetch = FetchType.LAZY)
    private SiteReportEntity siteReportEntity;

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
    }
}
