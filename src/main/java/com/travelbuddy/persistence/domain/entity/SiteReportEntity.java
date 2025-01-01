package com.travelbuddy.persistence.domain.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Builder
@Entity
@Table(name = "site_reports")
@ToString(exclude = {"reportEntity", "siteEntity"})
public class SiteReportEntity {
    @Id
    @Column(name = "report_id", nullable = false)
    private Integer reportId;

    @Column(name = "site_id", nullable = false)
    private Integer siteId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "report_id", referencedColumnName = "id", insertable = false, updatable = false)
    @MapsId
    private ReportEntity reportEntity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "site_id", referencedColumnName = "id", insertable = false, updatable = false)
    private SiteEntity siteEntity;
}
