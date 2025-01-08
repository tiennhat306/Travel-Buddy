package com.travelbuddy.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.travelbuddy.common.paging.PageDto;
import com.travelbuddy.common.utils.FilenameUtils;
import com.travelbuddy.common.utils.RequestUtils;
import com.travelbuddy.persistence.domain.dto.account.user.*;
import com.travelbuddy.systemlog.admin.SystemLogService;
import com.travelbuddy.upload.cloud.StorageService;
import com.travelbuddy.upload.cloud.dto.FileRspnDto;
import com.travelbuddy.upload.cloud.dto.FileUploadRqstDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/users")
@Slf4j
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final RequestUtils requestUtils;
    private final StorageService storageService;
    private final ObjectMapper objectMapper;
    private final SystemLogService systemLogService;

    @GetMapping("/detail")
    public ResponseEntity<UserDetailRspnDto> getUserDetail() {
        return ResponseEntity.ok(userService.getUserDetail(requestUtils.getUserIdCurrentRequest()));
    }

    @PutMapping("/change-password")
    public ResponseEntity<Void> changePassword(@RequestBody @Valid ChgPasswordRqstDto chgPasswordRqstDto) {
        userService.changePassword(requestUtils.getUserIdCurrentRequest(), chgPasswordRqstDto);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/basic-info")
    public ResponseEntity<Void> updateBasicInfo(@RequestBody @Valid UserBasicInfoUpdateRqstDto userBasicInfoUpdateRqstDto) {
        userService.updateBasicInfo(requestUtils.getUserIdCurrentRequest(), userBasicInfoUpdateRqstDto);

        return ResponseEntity.noContent().build();
    }

    @PutMapping("/email")
    public ResponseEntity<Void> updateEmail(@RequestBody @Valid ChgEmailRqstDto chgEmailRqstDto) {
        userService.updateEmail(requestUtils.getUserIdCurrentRequest(), chgEmailRqstDto.getEmail());

        return ResponseEntity.noContent().build();
    }

    @PutMapping("/detail")
    public ResponseEntity<Void> updateDetail(@RequestBody @Valid UserDetailUpdateRqstDto userDetailUpdateRqstDto) {
        userService.updateDetail(requestUtils.getUserIdCurrentRequest(), userDetailUpdateRqstDto);

        return ResponseEntity.noContent().build();
    }

    @PutMapping("/avatar")
    public ResponseEntity<Object> updateAvatar(@RequestParam("avatar") MultipartFile avatar) throws IOException {
        if (ObjectUtils.isEmpty(avatar)) {
            throw new IllegalArgumentException("Avatar is required");
        }

        String mimeType = avatar.getContentType();
        if (StringUtils.isEmpty(mimeType) || !mimeType.startsWith("image")) {
            throw new IllegalArgumentException("Avatar must be an image");
        }

        int userId = requestUtils.getUserIdCurrentRequest();

        FileUploadRqstDto fileUploadRqstDto = FileUploadRqstDto.builder()
                .inputStream(avatar.getInputStream())
                .folder("avatars")
                .mimeType(mimeType)
                .extension(FilenameUtils.getExtension(avatar.getOriginalFilename()).orElse(null))
                .build();

        FileRspnDto uploadedFile =  storageService.uploadFile(fileUploadRqstDto);

        userService.updateAvatar(userId, uploadedFile);

        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("image", uploadedFile.getUrl());

        return ResponseEntity.ok(objectNode);
    }

    @PutMapping("/unactivated")
    public ResponseEntity<Void> unactivated() {
        userService.unactivated(requestUtils.getUserIdCurrentRequest());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserPublicInfoRspnDto> getUserPublicInfo(@PathVariable int userId) {
        UserPublicInfoRspnDto userPublicInfo = userService.getUserPublicInfo(userId);

        return ResponseEntity.ok(userPublicInfo);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchUsers(@RequestParam(name = "q", required = false, defaultValue = "") String userSearch,
                                                               @RequestParam(name = "page", required = false, defaultValue = "1") int page) {
        if (StringUtils.isBlank(userSearch)) {
            return ResponseEntity.ok(List.of());
        }

        PageDto<UserSearchRspnDto> userSearchRspnDto = userService.searchUsers(userSearch, page);

        return ResponseEntity.ok(userSearchRspnDto);
    }
}
