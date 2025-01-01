package com.travelbuddy.persistence.domain.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Entity
@Table(name = "report_categories")
@ToString(exclude = {"types", "reports"})
public class ReportCategoryEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;

    private String description;

    private boolean enabled;

    @OneToMany(mappedBy = "category", fetch = FetchType.LAZY)
    @JsonBackReference
    private Set<ReportTypeEntity> types;

    @OneToMany(mappedBy = "reportCategoryEntity", fetch = FetchType.LAZY)
    @JsonBackReference
    private Set<ReportEntity> reports;
}
