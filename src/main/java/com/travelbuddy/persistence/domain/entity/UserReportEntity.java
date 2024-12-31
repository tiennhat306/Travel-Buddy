package com.travelbuddy.persistence.domain.entity;

import jakarta.persistence.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Builder
@Entity
@Table(name = "user_reports")
public class UserReportEntity {
    @Id
    @Column(name = "report_id", nullable = false)
    private Integer reportId;

    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "report_id", referencedColumnName = "id", insertable = false, updatable = false)
    @MapsId
    private ReportEntity reportEntity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id", insertable = false, updatable = false)
    private UserEntity userEntity;
}
