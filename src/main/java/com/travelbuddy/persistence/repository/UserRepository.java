package com.travelbuddy.persistence.repository;

import com.travelbuddy.persistence.domain.entity.UserEntity;
import org.apache.catalina.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Integer> {
    Optional<UserEntity> findByEmail(String email);

    boolean existsByEmail(String email);

    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN TRUE ELSE FALSE END FROM UserEntity u WHERE u.email = :email AND u.enabled = TRUE")
    boolean existsAndEnabledByEmail(String email);

    @Modifying
    @Query("DELETE FROM UserEntity u WHERE u.enabled = false AND u.createdAt < :thresholdDate")
    void deleteUnverifiedUsers(LocalDateTime thresholdDate);
    
    Optional<UserEntity> findById(Integer id);

    Page<UserEntity> searchAllByNicknameContainingIgnoreCaseOrEmailContainingIgnoreCase(String nickname, String email, Pageable pageable);
}
