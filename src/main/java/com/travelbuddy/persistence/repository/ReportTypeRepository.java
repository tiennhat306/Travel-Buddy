package com.travelbuddy.persistence.repository;

import com.travelbuddy.persistence.domain.entity.ReportTypeEntity;
import com.travelbuddy.persistence.domain.entity.ReportTypeId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ReportTypeRepository extends JpaRepository<ReportTypeEntity, ReportTypeId> {
    @Query("SELECT rt FROM ReportTypeEntity rt WHERE rt.id.type = :type")
    List<ReportTypeEntity> findAllByType(int type);

    @Query("SELECT CASE WHEN COUNT(rt) > 0 THEN TRUE ELSE FALSE END FROM ReportTypeEntity rt WHERE rt.id.categoryId = :categoryId AND rt.id.type = :type")
    boolean checkCategoryType(Integer categoryId, int type);
}
