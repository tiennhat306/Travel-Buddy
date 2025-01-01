package com.travelbuddy.persistence.repository;

import com.travelbuddy.persistence.domain.entity.ReviewReactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ReviewReactionRepository extends JpaRepository<ReviewReactionEntity, Long> {
    Optional<ReviewReactionEntity> findByUserIdAndReviewId(int userId, int reviewId);

    @Query("SELECT rre FROM ReviewReactionEntity rre WHERE rre.reviewId = ?1 AND rre.reactionType = ?2 ORDER BY rre.createdAt DESC LIMIT 1")
    ReviewReactionEntity findFirstBySiteReviewIdOrderByCreatedAtDesc(Integer entityId, String reactionType);

    int countByReviewIdAndReactionType(Integer entityId, String name);
}
