package com.travelbuddy.persistence.domain.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.Set;

@Entity
@Table(name = "groups")
@Data
@EqualsAndHashCode
@ToString(exclude = {"adminEntities"})
public class GroupEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "groups_permissions",
            joinColumns = @JoinColumn(name = "group_id"),
            inverseJoinColumns = @JoinColumn(name = "permission_id")
    )
    @EqualsAndHashCode.Exclude
    private Set<PermissionEntity> permissionEntities;

    @ManyToMany(mappedBy = "groupEntities")
    @JsonBackReference
    private Set<AdminEntity> adminEntities;
}