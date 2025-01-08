package com.travelbuddy.sitereviews;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.travelbuddy.common.constants.MediaTypeEnum;
import com.travelbuddy.common.paging.PageDto;
import com.travelbuddy.common.utils.FilenameUtils;
import com.travelbuddy.common.utils.RequestUtils;
import com.travelbuddy.persistence.domain.dto.sitereview.MySiteReviewRspnDto;
import com.travelbuddy.persistence.domain.dto.sitereview.SiteReviewCreateRqstDto;
import com.travelbuddy.persistence.domain.dto.sitereview.SiteReviewDetailRspnDto;
import com.travelbuddy.persistence.domain.dto.sitereview.SiteReviewUpdateRqstDto;
import com.travelbuddy.persistence.domain.entity.BehaviorLogEntity;
import com.travelbuddy.persistence.domain.entity.FileEntity;
import com.travelbuddy.persistence.domain.entity.ReviewMediaEntity;
import com.travelbuddy.persistence.repository.BehaviorLogRepository;
import com.travelbuddy.persistence.repository.SiteReviewRepository;
import com.travelbuddy.upload.cloud.StorageService;
import com.travelbuddy.upload.cloud.dto.FileRspnDto;
import com.travelbuddy.upload.cloud.dto.FileUploadRqstDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URI;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/site-reviews")
public class SiteReviewController {
    private final SiteReviewService siteReviewService;

    @PostMapping
    public ResponseEntity<Void> postSiteReview(@RequestBody @Valid SiteReviewCreateRqstDto siteReviewCreateRqstDto) {
        siteReviewService.createSiteReview(siteReviewCreateRqstDto);
        return ResponseEntity.created(URI.create("/api/site-reviews/" + siteReviewCreateRqstDto.getSiteId())).build();
    }

    @GetMapping("/{reviewId}")
    public ResponseEntity<Object> getSiteReview(@PathVariable int reviewId) {
        SiteReviewDetailRspnDto siteReview = siteReviewService.getSiteReviewById(reviewId);

        return ResponseEntity.ok(siteReview);
    }

    @PutMapping("/{reviewId}")
    public ResponseEntity<Void> updateSiteReview(@PathVariable int reviewId,
                                                 @RequestBody @Valid  SiteReviewUpdateRqstDto siteReviewUpdateRqstDto) {

        siteReviewService.updateSiteReview(reviewId, siteReviewUpdateRqstDto);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{reviewId}")
    public ResponseEntity<Void> deleteSiteReview(@PathVariable int reviewId) {
        siteReviewService.deleteSiteReview(reviewId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{reviewId}/like")
    public ResponseEntity<Void> likeSiteReview(@PathVariable int reviewId) {
        siteReviewService.likeSiteReview(reviewId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{reviewId}/dislike")
    public ResponseEntity<Void> dislikeSiteReview(@PathVariable int reviewId) {
        siteReviewService.dislikeSiteReview(reviewId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/my-reviews")
    public ResponseEntity<Object> getMySiteReviews(@RequestParam(name = "q", required = false, defaultValue = "") String reviewSearch,
                                                   @RequestParam(name = "page", required = false, defaultValue = "1") int page) {
        PageDto<MySiteReviewRspnDto> mySiteReviews = siteReviewService.getMySiteReviews(reviewSearch, page);

        return ResponseEntity.ok(mySiteReviews);
    }
}
