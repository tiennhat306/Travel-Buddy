package com.travelbuddy.persistence.domain.entity;

import com.travelbuddy.common.constants.ApprovalStatusEnum;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcType;
import org.hibernate.dialect.PostgreSQLEnumJdbcType;

import java.sql.Timestamp;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(name = "site_approvals")
@ToString(exclude = {"siteVersion"})
public class SiteApprovalEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "site_version_id", nullable = false)
    private Integer siteVersionId;

    @Column(name = "status")
    @JdbcType(PostgreSQLEnumJdbcType.class)
    @Enumerated(EnumType.STRING)
    private ApprovalStatusEnum status;

    @Column(name = "admin_id")
    private Integer adminId;

    @Column(name = "approved_at")
    private Timestamp approvedAt;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "site_version_id", insertable = false, updatable = false)
    private SiteVersionEntity siteVersion;
}
