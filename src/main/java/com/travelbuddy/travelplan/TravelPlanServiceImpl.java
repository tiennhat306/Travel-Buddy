package com.travelbuddy.travelplan;

import com.travelbuddy.common.constants.PlanRoleEnum;
import com.travelbuddy.common.exception.errorresponse.DataAlreadyExistsException;
import com.travelbuddy.common.exception.errorresponse.ForbiddenException;
import com.travelbuddy.common.exception.errorresponse.NotFoundException;
import com.travelbuddy.common.exception.userinput.UserInputException;
import com.travelbuddy.common.utils.RequestUtils;
import com.travelbuddy.persistence.domain.dto.site.SiteBasicInfoRspnDto;
import com.travelbuddy.persistence.domain.dto.travelplan.*;
import com.travelbuddy.persistence.domain.entity.*;
import com.travelbuddy.persistence.repository.*;
import com.travelbuddy.site.user.SiteService;
import com.travelbuddy.upload.cloud.StorageExecutorService;
import com.travelbuddy.upload.cloud.dto.FileRspnDto;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class TravelPlanServiceImpl implements TravelPlanService {
    private final TravelPlanRepository travelPlanRepository;
    private final UserRepository userRepository;
    private final RequestUtils requestUtils;
    private final TravelPlanUserRepository travelPlanUserRepository;
    private final SiteRepository siteRepository;
    private final TravelPlanSiteRepository travelPlanSiteRepository;
    private final SiteService siteService;
    private final StorageExecutorService storageExecutorService;
    private final FileRepository fileRepository;

    @Override
    public int createTravelPlan(TravelPlanCreateRqstDto travelPlanCreateRqstDto) {
        int requestUserId = requestUtils.getUserIdCurrentRequest();
        UserEntity userEntity = userRepository.findById(requestUserId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        if (travelPlanCreateRqstDto.getStartTime().isAfter(travelPlanCreateRqstDto.getEndTime())) {
            throw new UserInputException("Start time must be before end time");
        }

        if (!StringUtils.contains(travelPlanCreateRqstDto.getCover().getUrl(), "image")) {
            throw new UserInputException("Cover must be an image");
        }

        TravelPlanEntity travelPlanEntity = TravelPlanEntity.builder()
                .name(travelPlanCreateRqstDto.getName())
                .description(travelPlanCreateRqstDto.getDescription())
                .startTime(travelPlanCreateRqstDto.getStartTime())
                .endTime(travelPlanCreateRqstDto.getEndTime())
                .cover(FileEntity.builder()
                        .id(travelPlanCreateRqstDto.getCover().getId())
                        .url(travelPlanCreateRqstDto.getCover().getUrl())
                        .build())
                .build();

        List<UserEntity> userEntities = new ArrayList<>();
        userEntities.add(userEntity);

        travelPlanEntity.setUserEntities(userEntities);

        int travelPlanId =  travelPlanRepository.save(travelPlanEntity).getId();

        TravelPlanUserEntity travelPlanUserEntity = travelPlanUserRepository.findByTravelPlanIdAndUserId(travelPlanId, requestUserId)
                .orElseThrow(() -> new NotFoundException("Travel plan or User not found"));

        travelPlanUserEntity.setRole("OWNER");

        travelPlanUserRepository.save(travelPlanUserEntity);

        return travelPlanId;
    }

    @Override
    public void updateTravelPlan(int travelPlanId, TravelPlanUpdateRqstDto travelPlanUpdateRqstDto) {
        TravelPlanEntity travelPlanEntity = travelPlanRepository.findById(travelPlanId)
                .orElseThrow(() -> new NotFoundException("Travel plan not found"));

        int requestUserId = requestUtils.getUserIdCurrentRequest();
        if (!travelPlanUserRepository.isAdmin(travelPlanId, requestUserId)) {
            throw new ForbiddenException("Don't have permission to update travel plan");
        }

        if (travelPlanUpdateRqstDto.getStartTime().isAfter(travelPlanUpdateRqstDto.getEndTime())) {
            throw new UserInputException("Start time must be before end time");
        }

        List<TravelPlanSiteEntity> travelPlanSiteEntities = travelPlanSiteRepository.findAllByTravelPlanId(travelPlanId);
        for (TravelPlanSiteEntity travelPlanSiteEntity : travelPlanSiteEntities) {
            if (travelPlanSiteEntity.getStartTime().isBefore(travelPlanUpdateRqstDto.getStartTime())) {
                throw new UserInputException("Site start time must be after travel plan start time");
            }
            if (travelPlanSiteEntity.getEndTime().isAfter(travelPlanUpdateRqstDto.getEndTime())) {
                throw new UserInputException("Site end time must be before travel plan end time");
            }
        }

        travelPlanEntity.setName(travelPlanUpdateRqstDto.getName());
        travelPlanEntity.setDescription(travelPlanUpdateRqstDto.getDescription());
        travelPlanEntity.setStartTime(travelPlanUpdateRqstDto.getStartTime());
        travelPlanEntity.setEndTime(travelPlanUpdateRqstDto.getEndTime());

        travelPlanRepository.save(travelPlanEntity);
    }

    @Override
    public void changeCover(int travelPlanId, FileRspnDto uploadedFile) {
        TravelPlanEntity travelPlanEntity = travelPlanRepository.findById(travelPlanId)
                .orElseThrow(() -> new NotFoundException("Travel plan not found"));

        FileEntity newCover = FileEntity.builder()
                .id(uploadedFile.getId())
                .url(uploadedFile.getUrl())
                .build();

        if (travelPlanEntity.getCover() != null) {
            storageExecutorService.deleteFile(travelPlanEntity.getCover().getId());
            fileRepository.deleteById(travelPlanEntity.getCover().getId());
        }

        travelPlanEntity.setCover(newCover);

        travelPlanRepository.save(travelPlanEntity);
    }

    @Override
    public void addMemberToTravelPlan(int travelPlanId, int userId) {
        TravelPlanEntity travelPlanEntity = travelPlanRepository.findById(travelPlanId)
                .orElseThrow(() -> new NotFoundException("Travel plan not found"));

        UserEntity userEntity = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        int requestUserId = requestUtils.getUserIdCurrentRequest();
        if (!travelPlanUserRepository.isAdmin(travelPlanId, requestUserId)) {
            throw new ForbiddenException("Don't have permission to add member to travel plan");
        }

        if (travelPlanUserRepository.isMember(travelPlanId, userId)) {
            throw new DataAlreadyExistsException("User already in travel plan");
        }

        travelPlanUserRepository.save(TravelPlanUserEntity.builder()
                .id(TravelPlanUserId.builder()
                        .travelPlanEntity(travelPlanEntity)
                        .userEntity(userEntity)
                        .build())
                .role("MEMBER")
                .build());
    }

    @Override
    public void removeMemberFromTravelPlan(int travelPlanId, int userId) {
        travelPlanRepository.findById(travelPlanId)
                .orElseThrow(() -> new NotFoundException("Travel plan not found"));

        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        int requestUserId = requestUtils.getUserIdCurrentRequest();
        if (requestUserId == userId) {
            throw new UserInputException("Can't remove yourself from travel plan");
        }

        if (!travelPlanUserRepository.isAdmin(travelPlanId, requestUserId) || (travelPlanUserRepository.isAdmin(travelPlanId, userId) && !travelPlanUserRepository.isOwner(travelPlanId, requestUserId))) {
            throw new ForbiddenException("Don't have permission to remove member from travel plan");
        }

        if (!travelPlanUserRepository.isMember(travelPlanId, userId)) {
            throw new NotFoundException("User not in travel plan");
        }

        travelPlanUserRepository.deleteByTravelPlanIdAndUserId(travelPlanId, userId);
    }

    @Override
    public void changeMemberRole(int travelPlanId, ChgMemberRoleRqstDto chgMemberRoleRqstDto) {
        travelPlanRepository.findById(travelPlanId)
                .orElseThrow(() -> new NotFoundException("Travel plan not found"));

        userRepository.findById(chgMemberRoleRqstDto.getUserId())
                .orElseThrow(() -> new NotFoundException("User not found"));

        int requestUserId = requestUtils.getUserIdCurrentRequest();
        if (requestUserId == chgMemberRoleRqstDto.getUserId()) {
            throw new UserInputException("Can't change your own role");
        }

        if (!travelPlanUserRepository.isOwner(travelPlanId, requestUserId)) {
            throw new ForbiddenException("Don't have permission to change member role");
        }

        if (!travelPlanUserRepository.isMember(travelPlanId, chgMemberRoleRqstDto.getUserId())) {
            throw new NotFoundException("User not in travel plan");
        }

        if (Arrays.asList(PlanRoleEnum.ADMIN.name(), PlanRoleEnum.MEMBER.name()).contains(chgMemberRoleRqstDto.getRole())) {
            travelPlanUserRepository.updateRole(travelPlanId, chgMemberRoleRqstDto.getUserId(), chgMemberRoleRqstDto.getRole());
        } else {
            throw new UserInputException("Role must be ADMIN or MEMBER");
        }
    }

    @Override
    public void addSiteToTravelPlan(int travelPlanId, TravelPlanSiteCreateRqstDto travelPlanSiteCreateRqstDto) {
        TravelPlanEntity travelPlanEntity = travelPlanRepository.findById(travelPlanId)
                .orElseThrow(() -> new NotFoundException("Travel plan not found"));

        SiteEntity siteEntity = siteRepository.findById(travelPlanSiteCreateRqstDto.getSiteId())
                .orElseThrow(() -> new NotFoundException("Site not found"));

        int requestUserId = requestUtils.getUserIdCurrentRequest();
        if (!travelPlanUserRepository.isAdmin(travelPlanId, requestUserId)) {
            throw new ForbiddenException("Don't have permission to add site to travel plan");
        }

        if (travelPlanSiteCreateRqstDto.getStartTime().isBefore(travelPlanEntity.getStartTime())) {
            throw new UserInputException("Site start time must be after travel plan start time");
        }

        if (travelPlanSiteCreateRqstDto.getEndTime().isAfter(travelPlanEntity.getEndTime())) {
            throw new UserInputException("Site end time must be before travel plan end time");
        }

        if (travelPlanEntity.getSiteEntities().contains(siteEntity)) {
            throw new DataAlreadyExistsException("Site already in travel plan");
        }

        TravelPlanSiteEntity travelPlanSiteEntity = TravelPlanSiteEntity.builder()
                .id(TravelPlanSiteId.builder()
                        .travelPlanEntity(travelPlanEntity)
                        .siteEntity(siteEntity)
                        .build())
                .name(travelPlanSiteCreateRqstDto.getName())
                .description(travelPlanSiteCreateRqstDto.getDescription())
                .startTime(travelPlanSiteCreateRqstDto.getStartTime())
                .endTime(travelPlanSiteCreateRqstDto.getEndTime())
                .build();

        travelPlanSiteRepository.save(travelPlanSiteEntity);
    }

    @Override
    public void removeSiteFromTravelPlan(int travelPlanId, int siteId) {
        TravelPlanEntity travelPlanEntity = travelPlanRepository.findById(travelPlanId)
                .orElseThrow(() -> new NotFoundException("Travel plan not found"));

        SiteEntity siteEntity = siteRepository.findById(siteId)
                .orElseThrow(() -> new NotFoundException("Site not found"));

        int requestUserId = requestUtils.getUserIdCurrentRequest();
        if (!travelPlanUserRepository.isAdmin(travelPlanId, requestUserId)) {
            throw new ForbiddenException("Don't have permission to remove site from travel plan");
        }

        if (!travelPlanEntity.getSiteEntities().contains(siteEntity)) {
            throw new NotFoundException("Site not in travel plan");
        }

        travelPlanSiteRepository.deleteByTravelPlanIdAndSiteId(travelPlanId, siteId);
    }

    @Override
    public void updateSiteInTravelPlan(int travelPlanId, TravelPlanSiteUpdateRqstDto travelPlanSiteUpdateRqstDto) {
        TravelPlanEntity travelPlanEntity = travelPlanRepository.findById(travelPlanId)
                .orElseThrow(() -> new NotFoundException("Travel plan not found"));

        SiteEntity siteEntity = siteRepository.findById(travelPlanSiteUpdateRqstDto.getSiteId())
                .orElseThrow(() -> new NotFoundException("Site not found"));

        int requestUserId = requestUtils.getUserIdCurrentRequest();
        if (!travelPlanUserRepository.isAdmin(travelPlanId, requestUserId)) {
            throw new ForbiddenException("Don't have permission to update site in travel plan");
        }

        if (!travelPlanEntity.getSiteEntities().contains(siteEntity)) {
            throw new NotFoundException("Site not in travel plan");
        }

        TravelPlanSiteEntity travelPlanSiteEntity = travelPlanSiteRepository.findByTravelPlanIdAndSiteId(travelPlanId, travelPlanSiteUpdateRqstDto.getSiteId())
                .orElseThrow(() -> new NotFoundException("Site not in travel plan"));

        if (travelPlanSiteUpdateRqstDto.getStartTime().isBefore(travelPlanEntity.getStartTime())) {
            throw new UserInputException("Site start time must be after travel plan start time");
        }

        if (travelPlanSiteUpdateRqstDto.getEndTime().isAfter(travelPlanEntity.getEndTime())) {
            throw new UserInputException("Site end time must be before travel plan end time");
        }

        travelPlanSiteEntity.setName(travelPlanSiteUpdateRqstDto.getName());
        travelPlanSiteEntity.setDescription(travelPlanSiteUpdateRqstDto.getDescription());
        travelPlanSiteEntity.setStartTime(travelPlanSiteUpdateRqstDto.getStartTime());
        travelPlanSiteEntity.setEndTime(travelPlanSiteUpdateRqstDto.getEndTime());

        travelPlanSiteRepository.save(travelPlanSiteEntity);
    }

    @Override
    public void deleteTravelPlan(int travelPlanId) {
        travelPlanRepository.findById(travelPlanId)
                .orElseThrow(() -> new NotFoundException("Travel plan not found"));

        int requestUserId = requestUtils.getUserIdCurrentRequest();
        if (!travelPlanUserRepository.isOwner(travelPlanId, requestUserId)) {
            throw new ForbiddenException("Don't have permission to delete travel plan");
        }

        travelPlanRepository.deleteById(travelPlanId);
    }

    @Override
    public TravelPlanRspnDto getTravelPlan(int travelPlanId) {
        TravelPlanEntity travelPlanEntity = travelPlanRepository.findById(travelPlanId)
                .orElseThrow(() -> new NotFoundException("Travel plan not found"));

        TravelPlanRspnDto travelPlanRspnDto = new TravelPlanRspnDto();
        travelPlanRspnDto.setName(travelPlanEntity.getName());
        travelPlanRspnDto.setDescription(travelPlanEntity.getDescription());
        travelPlanRspnDto.setStartTime(String.valueOf(travelPlanEntity.getStartTime()));
        travelPlanRspnDto.setEndTime(String.valueOf(travelPlanEntity.getEndTime()));
        travelPlanRspnDto.setCoverUrl(travelPlanEntity.getCover() != null ? travelPlanEntity.getCover().getUrl() : null);

        List<TravelPlanSiteRspnDto> sites = new ArrayList<>();
        for (SiteEntity siteEntity : travelPlanEntity.getSiteEntities()) {
            TravelPlanSiteEntity travelPlanSiteEntity = travelPlanSiteRepository.findByTravelPlanIdAndSiteId(travelPlanId, siteEntity.getId())
                    .orElseThrow(() -> new NotFoundException("Site not in travel plan"));

            TravelPlanSiteRspnDto siteRspnDto = new TravelPlanSiteRspnDto();
            siteRspnDto.setName(travelPlanSiteEntity.getName());
            siteRspnDto.setDescription(travelPlanSiteEntity.getDescription());
            siteRspnDto.setStartTime(String.valueOf(travelPlanSiteEntity.getStartTime()));
            siteRspnDto.setEndTime(String.valueOf(travelPlanSiteEntity.getEndTime()));

            SiteBasicInfoRspnDto siteBasicInfoRspnDto = siteService.getSiteBasicRepresentation(siteEntity.getId());
            siteRspnDto.setSiteBasicInfoRspnDto(siteBasicInfoRspnDto);

            sites.add(siteRspnDto);
        }

        sites.sort(Comparator.comparing(TravelPlanSiteRspnDto::getStartTime));
        travelPlanRspnDto.setSites(sites);

        List<TravelPlanMemberRspnDto> members = new ArrayList<>();
        for (UserEntity userEntity : travelPlanEntity.getUserEntities()) {
            TravelPlanMemberRspnDto travelPlanMemberRspnDto = new TravelPlanMemberRspnDto();
            travelPlanMemberRspnDto.setUserId(userEntity.getId());
            travelPlanMemberRspnDto.setNickname(userEntity.getNickname());
            travelPlanMemberRspnDto.setAvatarUrl(userEntity.getAvatar() != null ? userEntity.getAvatar().getUrl() : null);
            travelPlanMemberRspnDto.setRole(travelPlanUserRepository.getRole(travelPlanId, userEntity.getId()));

            members.add(travelPlanMemberRspnDto);
        }
        travelPlanRspnDto.setMembers(members);

        return travelPlanRspnDto;
    }

    @Override
    public List<TravelPlanBasicRspnDto> getTravelPlans() {
        int requestUserId = requestUtils.getUserIdCurrentRequest();

        List<TravelPlanEntity> travelPlanEntities = travelPlanRepository.findAllByUserId(requestUserId);

        return travelPlanEntities.stream()
                .map(travelPlanEntity -> {
                    TravelPlanBasicRspnDto travelPlanBasicRspnDto = new TravelPlanBasicRspnDto();
                    travelPlanBasicRspnDto.setId(travelPlanEntity.getId());
                    travelPlanBasicRspnDto.setName(travelPlanEntity.getName());
                    travelPlanBasicRspnDto.setDescription(travelPlanEntity.getDescription());
                    travelPlanBasicRspnDto.setStartTime(String.valueOf(travelPlanEntity.getStartTime()));
                    travelPlanBasicRspnDto.setEndTime(String.valueOf(travelPlanEntity.getEndTime()));
                    travelPlanBasicRspnDto.setCoverUrl(travelPlanEntity.getCover() != null ? travelPlanEntity.getCover().getUrl() : null);

                    return travelPlanBasicRspnDto;
                })
                .toList();
    }
}
