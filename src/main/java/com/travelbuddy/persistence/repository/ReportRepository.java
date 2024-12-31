package com.travelbuddy.persistence.repository;

import com.travelbuddy.persistence.domain.entity.ReportEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReportRepository extends JpaRepository<ReportEntity, Integer> {
}
