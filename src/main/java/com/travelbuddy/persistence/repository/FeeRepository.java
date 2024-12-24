package com.travelbuddy.persistence.repository;

import com.travelbuddy.persistence.domain.entity.FeeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FeeRepository extends JpaRepository<FeeEntity, Integer> {
    Optional<List<FeeEntity>> findAllBySiteVersionId(Integer siteVersionId);
    // return if there is a record with AspectID in fee
    boolean existsByAspectId(Integer aspectId);
}
