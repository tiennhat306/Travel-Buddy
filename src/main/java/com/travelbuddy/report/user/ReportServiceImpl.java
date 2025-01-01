package com.travelbuddy.report.user;

import com.travelbuddy.common.constants.ReportTypeEnum;
import com.travelbuddy.common.exception.errorresponse.NotFoundException;
import com.travelbuddy.common.utils.RequestUtils;
import com.travelbuddy.persistence.domain.dto.report.user.SiteReportRqstDto;
import com.travelbuddy.persistence.domain.dto.report.user.SiteReviewReportRqstDto;
import com.travelbuddy.persistence.domain.dto.report.user.UserReportRqstDto;
import com.travelbuddy.persistence.domain.entity.*;
import com.travelbuddy.persistence.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {
    private final UserRepository userRepository;
    private final RequestUtils requestUtils;
    private final SiteReportRepository siteReportRepository;
    private final SiteRepository siteRepository;
    private final ReportRepository reportRepository;
    private final SiteReviewRepository siteReviewRepository;
    private final UserReportRepository userReportRepository;
    private final SiteReviewReportRepository siteReviewReportRepository;
    private final ReportCategoryRepository reportCategoryRepository;
    private final ReportTypeRepository reportTypeRepository;

    @Override
    public void reportSite(SiteReportRqstDto siteReportRqstDto) {
        // check category id
        boolean isValid =  reportTypeRepository.checkCategoryType(siteReportRqstDto.getCategoryId(), ReportTypeEnum.SITE.getType());
        if (!isValid) {
            throw new NotFoundException("Category does not match with site report type");
        }

        SiteReportEntity siteReportEntity = siteReportRepository.findBySiteIdAndUserId(siteReportRqstDto.getSiteId(), requestUtils.getUserIdCurrentRequest());
        if (siteReportEntity != null) {
            siteReportEntity.getReportEntity().setCategoryId(siteReportRqstDto.getCategoryId());
            siteReportEntity.getReportEntity().setDescription(siteReportRqstDto.getDescription());
            reportRepository.save(siteReportEntity.getReportEntity());
            return;
        }

        ReportEntity reportEntity = ReportEntity.builder()
                .categoryId(siteReportRqstDto.getCategoryId())
                .description(siteReportRqstDto.getDescription())
                .user(userRepository.findById(requestUtils.getUserIdCurrentRequest())
                        .orElseThrow(() -> new NotFoundException("User not found")))
                .build();

        reportEntity = reportRepository.save(reportEntity);
        reportRepository.flush();

        SiteReportEntity newSiteReportEntity = SiteReportEntity.builder()
                .reportEntity(reportEntity)
                .siteId(siteReportRqstDto.getSiteId())
                .siteEntity(siteRepository.findById(siteReportRqstDto.getSiteId())
                        .orElseThrow(() -> new NotFoundException("Site not found")))
                .build();

        siteReportRepository.save(newSiteReportEntity);
    }

    @Override
    public void reportSiteReview(SiteReviewReportRqstDto siteReviewReportRqstDto) {
        // check category id
        boolean isValid =  reportTypeRepository.checkCategoryType(siteReviewReportRqstDto.getCategoryId(), ReportTypeEnum.SITE_REVIEW.getType());
        if (!isValid) {
            throw new NotFoundException("Category does not match with site review report type");
        }

        SiteReviewReportEntity siteReviewReportEntity = siteReviewReportRepository.findBySiteReviewIdAndUserId(siteReviewReportRqstDto.getSiteReviewId(), requestUtils.getUserIdCurrentRequest());
        if (siteReviewReportEntity != null) {
            siteReviewReportEntity.getReportEntity().setCategoryId(siteReviewReportRqstDto.getCategoryId());
            siteReviewReportEntity.getReportEntity().setDescription(siteReviewReportRqstDto.getDescription());
            reportRepository.save(siteReviewReportEntity.getReportEntity());
            return;
        }

        ReportEntity reportEntity = ReportEntity.builder()
                .categoryId(siteReviewReportRqstDto.getCategoryId())
                .description(siteReviewReportRqstDto.getDescription())
                .user(userRepository.findById(requestUtils.getUserIdCurrentRequest())
                        .orElseThrow(() -> new NotFoundException("User not found")))
                .build();

        reportEntity = reportRepository.save(reportEntity);
        reportRepository.flush();

        SiteReviewReportEntity newSiteReviewReportEntity = SiteReviewReportEntity.builder()
                .reportEntity(reportEntity)
                .siteReviewId(siteReviewReportRqstDto.getSiteReviewId())
                .siteReviewEntity(siteReviewRepository.findById(siteReviewReportRqstDto.getSiteReviewId())
                        .orElseThrow(() -> new NotFoundException("Site review not found")))
                .build();

        siteReviewReportRepository.save(newSiteReviewReportEntity);
    }

    @Override
    public void reportUser(UserReportRqstDto userReportRqstDto) {
        // check category id
        boolean isValid =  reportTypeRepository.checkCategoryType(userReportRqstDto.getCategoryId(), ReportTypeEnum.USER.getType());
        if (!isValid) {
            throw new NotFoundException("Category does not match with user report type");
        }

        UserReportEntity userReportEntity = userReportRepository.findByUserIdAndUserId(userReportRqstDto.getUserId(), requestUtils.getUserIdCurrentRequest());
        if (userReportEntity != null) {
            userReportEntity.getReportEntity().setCategoryId(userReportRqstDto.getCategoryId());
            userReportEntity.getReportEntity().setDescription(userReportRqstDto.getDescription());
            reportRepository.save(userReportEntity.getReportEntity());
            return;
        }

        ReportEntity reportEntity = ReportEntity.builder()
                .categoryId(userReportRqstDto.getCategoryId())
                .description(userReportRqstDto.getDescription())
                .user(userRepository.findById(requestUtils.getUserIdCurrentRequest())
                        .orElseThrow(() -> new NotFoundException("User not found")))
                .build();

        reportEntity = reportRepository.save(reportEntity);
        reportRepository.flush();

        UserReportEntity newUserReportEntity = UserReportEntity.builder()
                .reportEntity(reportEntity)
                .userId(userReportRqstDto.getUserId())
                .userEntity(userRepository.findById(userReportRqstDto.getUserId())
                        .orElseThrow(() -> new NotFoundException("User not found")))
                .build();

        userReportRepository.save(newUserReportEntity);
    }
}
