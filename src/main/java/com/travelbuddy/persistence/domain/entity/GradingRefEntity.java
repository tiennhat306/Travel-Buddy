package com.travelbuddy.persistence.domain.entity;

import jakarta.persistence.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Entity
@Builder
@Table(name = "grading_ref")
public class GradingRefEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    
    @Column(name = "action_name")
    private String actionName;
    
    @Column(name = "description")
    private String description;
    
    @Column(name = "prize_point")
    private Integer prizePoint;
}
