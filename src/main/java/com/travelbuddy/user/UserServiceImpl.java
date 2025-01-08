package com.travelbuddy.user;

import com.travelbuddy.common.constants.PaginationLimitConstants;
import com.travelbuddy.common.exception.auth.InvalidLoginCredentialsException;
import com.travelbuddy.common.exception.errorresponse.NotFoundException;
import com.travelbuddy.common.mapper.PageMapper;
import com.travelbuddy.common.paging.PageDto;
import com.travelbuddy.mapper.UserMapper;
import com.travelbuddy.persistence.domain.dto.account.user.*;
import com.travelbuddy.persistence.domain.dto.auth.BasicInfoDto;
import com.travelbuddy.persistence.domain.entity.FileEntity;
import com.travelbuddy.persistence.domain.entity.UserEntity;
import com.travelbuddy.persistence.repository.FileRepository;
import com.travelbuddy.persistence.repository.UserRepository;
import com.travelbuddy.systemlog.admin.SystemLogService;
import com.travelbuddy.upload.cloud.StorageExecutorService;
import com.travelbuddy.upload.cloud.dto.FileRspnDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final FileRepository fileRepository;
    private final StorageExecutorService storageExecutorService;
    private final PageMapper pageMapper;
    private final SystemLogService systemLogService;

    @Override
    public int getUserIdByEmailOrUsername(String email) {
        return userRepository
                .findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User with email not found"))
                .getId();
    }

    @Override
    public boolean isUserExists(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public void changePassword(int userId, ChgPasswordRqstDto chgPasswordRqstDto) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        if (!passwordEncoder.matches(chgPasswordRqstDto.getOldPassword(), user.getPassword())) {
            throw new InvalidLoginCredentialsException("Old password is incorrect");
        }

        user.setPassword(passwordEncoder.encode(chgPasswordRqstDto.getNewPassword()));
        userRepository.save(user);
        systemLogService.logInfo(userId + " changed password");
    }

    @Override
    public void updateEmail(int userId, String email) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        user.setEmail(email);

        userRepository.save(user);
    }

    @Override
    public UserDetailRspnDto getUserDetail(int userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        return userMapper.toUserDetailRspnDto(user);
    }

    @Override
    public void updateBasicInfo(int userId, UserBasicInfoUpdateRqstDto userBasicInfoUpdateRqstDto) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        user.setNickname(userBasicInfoUpdateRqstDto.getNickname());
        user.setFullName(userBasicInfoUpdateRqstDto.getFullName());

        userRepository.save(user);
    }

    @Override
    public void updateDetail(int userId, UserDetailUpdateRqstDto userDetailUpdateRqstDto) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        user.setGender(userDetailUpdateRqstDto.getGender());
        user.setPhoneNumber(userDetailUpdateRqstDto.getPhoneNumber());
        user.setBirthDate(userDetailUpdateRqstDto.getBirthDate());
        user.setAddress(userDetailUpdateRqstDto.getAddress());
        user.setSocialUrl(userDetailUpdateRqstDto.getSocialUrl());

        userRepository.save(user);
    }

    @Override
    public void updateAvatar(int userId, FileRspnDto uploadedFile) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        FileEntity newAvatar = FileEntity.builder()
                .id(uploadedFile.getId())
                .url(uploadedFile.getUrl())
                .build();

        if (user.getAvatar() != null) {
            storageExecutorService.deleteFile(user.getAvatar().getId());
            fileRepository.deleteById(user.getAvatar().getId());
        }

        user.setAvatar(newAvatar);

        userRepository.save(user);
    }

    @Override
    public void unactivated(int userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        user.setEnabled(false);

        userRepository.save(user);
        systemLogService.logInfo(userId + " unactivated account");
    }

    @Override
    public BasicInfoDto getUserBasicInfo(String email) {
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User not found"));

        return userMapper.toBasicInfoDto(user);
    }

    @Override
    public UserPublicInfoRspnDto getUserPublicInfo(int userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        return userMapper.toUserPublicInfoRspnDto(user);
    }

    @Override
    public PageDto<UserSearchRspnDto> searchUsers(String userSearch, int page) {
        Pageable pageable = PageRequest.of(page - 1, PaginationLimitConstants.USER_SEARCH_LIMIT);

        Page<UserEntity> userEntities = userRepository.searchAllByNicknameContainingIgnoreCaseOrEmailContainingIgnoreCase(userSearch, userSearch, pageable);

        return pageMapper.toPageDto(userEntities.map(userMapper::toUserSearchRspnDto));
    }
}
