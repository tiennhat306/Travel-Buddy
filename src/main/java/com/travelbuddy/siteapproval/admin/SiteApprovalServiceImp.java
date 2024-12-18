package com.travelbuddy.siteapproval.admin;

import com.travelbuddy.common.constants.ApprovalStatusEnum;
import com.travelbuddy.common.constants.PaginationLimitConstants;
import com.travelbuddy.common.exception.errorresponse.EnumNotFitException;
import com.travelbuddy.common.exception.errorresponse.NotFoundException;
import com.travelbuddy.common.mapper.PageMapper;
import com.travelbuddy.common.paging.PageDto;
import com.travelbuddy.persistence.domain.dto.siteapproval.GeneralViewSiteApprovalRspndDto;
import com.travelbuddy.persistence.domain.dto.siteapproval.UpdateSiteApprovalRqstDto;
import com.travelbuddy.persistence.domain.entity.SiteApprovalEntity;
import com.travelbuddy.persistence.repository.SiteApprovalRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Service
public class SiteApprovalServiceImp implements SiteApprovalService {
    private final SiteApprovalRepository siteApprovalRepository;
    private final PageMapper pageMapper;

    public SiteApprovalServiceImp(SiteApprovalRepository siteApprovalRepository, PageMapper pageMapper) {
        this.siteApprovalRepository = siteApprovalRepository;
        this.pageMapper = pageMapper;
    }

    @Override
    public void createDefaultSiteApproval(Integer siteVersionId) {
        /*
        * This function creates default site_approval record when a new site is created
        */
        SiteApprovalEntity siteApprovalEntity = new SiteApprovalEntity();
        siteApprovalEntity = SiteApprovalEntity.builder().siteVersionId(siteVersionId).build();

        // Save the site approval
        siteApprovalRepository.save(siteApprovalEntity);
    }

    @Override
    public void updateSiteApproval(UpdateSiteApprovalRqstDto updateSiteApprovalRqstDto, Integer adminId) {
        /*
        * This function updates the site approval record when admin approves or rejects a site
        * with adminId get from the JWTToken
        */
        try {
            ApprovalStatusEnum.valueOf(updateSiteApprovalRqstDto.getStatus());
        } catch (IllegalArgumentException e) {
            throw new EnumNotFitException("Invalid status: " + updateSiteApprovalRqstDto.getStatus());
        }

        SiteApprovalEntity siteApprovalEntity = siteApprovalRepository.findById(updateSiteApprovalRqstDto.getId()).orElseThrow(() -> new NotFoundException("Site approval not found"));
        siteApprovalEntity.setStatus(ApprovalStatusEnum.valueOf(updateSiteApprovalRqstDto.getStatus()));
        siteApprovalEntity.setAdminId(adminId);
        siteApprovalEntity.setApprovedAt(Timestamp.valueOf(LocalDateTime.now()));
        siteApprovalRepository.save(siteApprovalEntity);
    }

    @Override
    public PageDto<GeneralViewSiteApprovalRspndDto> getPendingSiteApprovals(int page) {
        Pageable pageable = PageRequest.of(page - 1, PaginationLimitConstants.SITE_APPROVAL_LIMIT);
        Page<SiteApprovalEntity> siteApprovalEntities = siteApprovalRepository.findAllByStatus(null, pageable);
        return pageMapper.toPageDto(siteApprovalEntities.map(GeneralViewSiteApprovalRspndDto::new));
    }
}
