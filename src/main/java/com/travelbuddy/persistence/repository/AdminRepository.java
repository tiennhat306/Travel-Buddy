package com.travelbuddy.persistence.repository;

import com.travelbuddy.persistence.domain.entity.AdminEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface AdminRepository extends JpaRepository<AdminEntity, Integer> {
    Optional<AdminEntity> findByEmail(String email);
    Page<AdminEntity> findAll(Pageable pageable);
    Page<AdminEntity> findAllByEmailContaining(String search, Pageable pageable);

    @Query("SELECT CASE WHEN COUNT(a) > 0 THEN TRUE ELSE FALSE END FROM AdminEntity a WHERE a.email = :email AND a.enabled = TRUE")
    boolean existsAndEnabledByEmail(String email);
}
