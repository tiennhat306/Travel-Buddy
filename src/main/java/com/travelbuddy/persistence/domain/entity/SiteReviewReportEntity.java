package com.travelbuddy.persistence.domain.entity;

import jakarta.persistence.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Entity
@Builder
@Table(name = "site_review_reports")
public class SiteReviewReportEntity {
    @Id
    @Column(name = "report_id", nullable = false)
    private Integer reportId;

    @Column(name = "site_review_id", nullable = false)
    private Integer siteReviewId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "report_id", referencedColumnName = "id", insertable = false, updatable = false)
    @MapsId
    private ReportEntity reportEntity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "site_review_id", referencedColumnName = "id", insertable = false, updatable = false)
    private SiteReviewEntity siteReviewEntity;
}
