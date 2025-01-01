package com.travelbuddy.persistence.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Entity
@Builder
@Table(name = "system_logs")
public class LogEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "timestamp")
    private Timestamp timestamp;

    @Column(name = "level")
    private String level;

    @Column(name = "content")
    private String content;

    @Override
    public String toString() {
        return String.format("[%s] %s: %s\n", timestamp, level, content);
    }
}
