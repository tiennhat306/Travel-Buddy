package com.travelbuddy.persistence.domain.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Embeddable
public class ReportTypeId implements Serializable {
    @Column(name = "type", nullable = false)
    private Integer type;

    @Column(name = "category_id", nullable = false)
    private Integer categoryId;
}
