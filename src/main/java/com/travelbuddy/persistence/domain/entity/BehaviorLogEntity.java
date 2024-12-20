package com.travelbuddy.persistence.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;

@Getter
@Setter
@Entity
@Table(name = "user_behavior_logs")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BehaviorLogEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "timestamp")
    private Timestamp timestamp;

    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "extra_info")
    private String extraInfo;

    @Column(name = "site_id")
    private Integer siteId;

    @Column(name = "behavior")
    private String behavior;
}
