package com.travelbuddy.report.admin;

import com.travelbuddy.common.exception.errorresponse.NotFoundException;
import com.travelbuddy.common.mapper.PageMapper;
import com.travelbuddy.common.paging.PageDto;
import com.travelbuddy.common.utils.RequestUtils;
import com.travelbuddy.persistence.domain.dto.report.admin.ReportDetailRspnDto;
import com.travelbuddy.persistence.domain.dto.report.admin.SiteReportRspnDto;
import com.travelbuddy.persistence.domain.dto.report.admin.SiteReviewReportRspnDto;
import com.travelbuddy.persistence.domain.dto.report.admin.UserReportRspnDto;
import com.travelbuddy.persistence.domain.entity.*;
import com.travelbuddy.persistence.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.travelbuddy.common.constants.PaginationLimitConstants.REPORT_LIMIT;

@Service
@Transactional
@RequiredArgsConstructor
public class AdminReportServiceImpl implements AdminReportService {
    private final UserRepository userRepository;
    private final RequestUtils requestUtils;
    private final SiteReportRepository siteReportRepository;
    private final SiteRepository siteRepository;
    private final ReportRepository reportRepository;
    private final SiteReviewRepository siteReviewRepository;
    private final UserReportRepository userReportRepository;
    private final SiteReviewReportRepository siteReviewReportRepository;
    private final PageMapper pageMapper;


    @Override
    public PageDto<SiteReportRspnDto> getSiteReports(String search, int page) {
        Pageable pageable = PageRequest.of(page - 1, REPORT_LIMIT);

        Page<SiteReportRspnDto> siteReportEntities = siteReportRepository.findAllBySiteNameAndPageable(search, pageable);

        return pageMapper.toPageDto(siteReportEntities);
    }

    @Override
    public PageDto<SiteReportRspnDto> getBannedSiteReports(String search, int page) {
        Pageable pageable = PageRequest.of(page - 1, REPORT_LIMIT);

        Page<SiteReportRspnDto> siteReportEntities = siteReportRepository.findAllBannedBySiteNameAndPageable(search, pageable);

        return pageMapper.toPageDto(siteReportEntities);
    }

    @Override
    public PageDto<ReportDetailRspnDto> getSiteReportsBySiteId(Integer siteId, int page) {
        SiteEntity siteEntity = siteRepository.findById(siteId).orElseThrow(() -> new NotFoundException("Site not found"));

        Pageable pageable = PageRequest.of(page - 1, REPORT_LIMIT);

        Page<SiteReportEntity> siteReportEntities = siteReportRepository.findAllBySiteId(siteId, pageable);

        return pageMapper.toPageDto(siteReportEntities.map(siteReportEntity -> {
            ReportEntity reportEntity = siteReportEntity.getReportEntity();

            return ReportDetailRspnDto.builder()
                    .reportId(reportEntity.getId())
                    .userId(reportEntity.getReportedBy())
                    .userName(reportEntity.getUser().getNickname())
                    .userImageUrl(reportEntity.getUser().getAvatar() != null ? reportEntity.getUser().getAvatar().getUrl() : null)
                    .categoryId(reportEntity.getReportCategoryEntity().getId())
                    .categoryName(reportEntity.getReportCategoryEntity().getName())
                    .description(reportEntity.getDescription())
                    .createdAt(String.valueOf(reportEntity.getCreatedAt()))
                    .build();
        }));
    }

    @Override
    public void banSite(Integer siteId) {
        SiteEntity siteEntity = siteRepository.findById(siteId).orElseThrow(() -> new NotFoundException("Site not found"));

        siteEntity.setEnabled(false);
        siteRepository.save(siteEntity);
    }

    @Override
    public void unbanSite(Integer siteId) {
        SiteEntity siteEntity = siteRepository.findById(siteId).orElseThrow(() -> new NotFoundException("Site not found"));

        siteEntity.setEnabled(true);
        siteRepository.save(siteEntity);
    }

    @Override
    public PageDto<SiteReviewReportRspnDto> getSiteReviewReports(String search, int page) {
        Pageable pageable = PageRequest.of(page - 1, REPORT_LIMIT);

        Page<SiteReviewReportRspnDto> siteReviewReportEntities = siteReviewReportRepository.findAllBySiteNameAndPageable(search, pageable);

        return pageMapper.toPageDto(siteReviewReportEntities);
    }

    @Override
    public PageDto<SiteReviewReportRspnDto> getBannedSiteReviewReports(String search, int page) {
        Pageable pageable = PageRequest.of(page - 1, REPORT_LIMIT);

        Page<SiteReviewReportRspnDto> siteReviewReportEntities = siteReviewReportRepository.findAllBannedBySiteNameAndPageable(search, pageable);

        return pageMapper.toPageDto(siteReviewReportEntities);
    }

    @Override
    public void banSiteReview(Integer siteReviewId) {
        SiteReviewEntity siteReviewEntity = siteReviewRepository.findById(siteReviewId).orElseThrow(() -> new NotFoundException("Site review not found"));

        siteReviewEntity.setEnabled(false);
        siteReviewRepository.save(siteReviewEntity);
    }

    @Override
    public void unbanSiteReview(Integer siteReviewId) {
        SiteReviewEntity siteReviewEntity = siteReviewRepository.findById(siteReviewId).orElseThrow(() -> new NotFoundException("Site review not found"));

        siteReviewEntity.setEnabled(true);
        siteReviewRepository.save(siteReviewEntity);
    }

    @Override
    public PageDto<UserReportRspnDto> getUserReports(String search, int page) {
        Pageable pageable = PageRequest.of(page - 1, REPORT_LIMIT);

        Page<UserReportRspnDto> userReportEntities = userReportRepository.findAllByUserNicknameAndPageable(search, pageable);

        return pageMapper.toPageDto(userReportEntities);
    }

    @Override
    public PageDto<UserReportRspnDto> getBannedUserReports(String search, int page) {
        Pageable pageable = PageRequest.of(page - 1, REPORT_LIMIT);

        Page<UserReportRspnDto> userReportEntities = userReportRepository.findAllBannedByUserNicknameAndPageable(search, pageable);

        return pageMapper.toPageDto(userReportEntities);
    }

    @Override
    public void banUser(Integer userId) {
        UserEntity userEntity = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));

        userEntity.setEnabled(false);
        userRepository.save(userEntity);
    }

    @Override
    public void unbanUser(Integer userId) {
        UserEntity userEntity = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));

        userEntity.setEnabled(true);
        userRepository.save(userEntity);
    }

    @Override
    public PageDto<ReportDetailRspnDto> getSiteReviewReportsBySiteReviewId(Integer siteReviewId, int page) {
        SiteReviewEntity siteReviewEntity = siteReviewRepository.findById(siteReviewId).orElseThrow(() -> new NotFoundException("Site review not found"));

        Pageable pageable = PageRequest.of(page - 1, REPORT_LIMIT);

        Page<SiteReviewReportEntity> siteReviewReportEntities = siteReviewReportRepository.findAllBySiteReviewId(siteReviewId, pageable);

        return pageMapper.toPageDto(siteReviewReportEntities.map(siteReviewReportEntity -> {
            ReportEntity reportEntity = siteReviewReportEntity.getReportEntity();

            return ReportDetailRspnDto.builder()
                    .reportId(reportEntity.getId())
                    .userId(reportEntity.getReportedBy())
                    .userName(reportEntity.getUser().getNickname())
                    .userImageUrl(reportEntity.getUser().getAvatar() != null ? reportEntity.getUser().getAvatar().getUrl() : null)
                    .categoryId(reportEntity.getReportCategoryEntity().getId())
                    .categoryName(reportEntity.getReportCategoryEntity().getName())
                    .description(reportEntity.getDescription())
                    .createdAt(String.valueOf(reportEntity.getCreatedAt()))
                    .build();
        }));
    }

    @Override
    public PageDto<ReportDetailRspnDto> getUserReportsByUserId(Integer userId, int page) {
        UserEntity userEntity = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));

        Pageable pageable = PageRequest.of(page - 1, REPORT_LIMIT);

        Page<UserReportEntity> userReportEntities = userReportRepository.findAllByUserId(userId, pageable);

        return pageMapper.toPageDto(userReportEntities.map(userReportEntity -> {
            ReportEntity reportEntity = userReportEntity.getReportEntity();

            return ReportDetailRspnDto.builder()
                    .reportId(reportEntity.getId())
                    .userId(reportEntity.getReportedBy())
                    .userName(reportEntity.getUser().getNickname())
                    .userImageUrl(reportEntity.getUser().getAvatar() != null ? reportEntity.getUser().getAvatar().getUrl() : null)
                    .categoryId(reportEntity.getReportCategoryEntity().getId())
                    .categoryName(reportEntity.getReportCategoryEntity().getName())
                    .description(reportEntity.getDescription())
                    .createdAt(String.valueOf(reportEntity.getCreatedAt()))
                    .build();
        }));
    }
}
