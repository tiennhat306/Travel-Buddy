package com.travelbuddy.persistence.repository;

import com.travelbuddy.persistence.domain.entity.ReportCategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportCategoryRepository extends JpaRepository<ReportCategoryEntity, Integer> {
}
