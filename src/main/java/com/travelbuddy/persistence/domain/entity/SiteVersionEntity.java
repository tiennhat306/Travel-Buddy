package com.travelbuddy.persistence.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "site_versions")
@ToString(exclude = {"siteType", "siteEntity", "siteApprovalEntity", "fees"})
public class SiteVersionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "site_id")
    private Integer siteId;

    @Column(name = "parent_version_id")
    private Integer parentVersionId;

    @Column(name = "site_name")
    private String siteName;

    private double lat;
    private double lng;

    @Column(name = "resolved_address")
    private String resolvedAddress;

    private String website;

    @Column(name = "site_description")
    private String description;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "type_id")
    private Integer typeId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "type_id", insertable = false, updatable = false)
    private SiteTypeEntity siteType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "site_id", insertable = false, updatable = false)
    private SiteEntity siteEntity;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "id", referencedColumnName = "site_version_id")
    private SiteApprovalEntity siteApprovalEntity;

    @OneToMany(mappedBy = "siteVersion", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FeeEntity> fees;
}
