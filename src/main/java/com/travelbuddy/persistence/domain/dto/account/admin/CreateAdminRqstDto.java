package com.travelbuddy.persistence.domain.dto.account.admin;

import com.travelbuddy.persistence.domain.entity.AdminEntity;
import com.travelbuddy.persistence.domain.entity.FileEntity;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
public class CreateAdminRqstDto {
    private String nickname;
    private String fullName;
    private String email;
    private String gender;
    private String address;
    private String phoneNumber;
    private String avatarId;
    private String avatarUrl;
    private String password;
    private String birthday;
    private boolean enabled;

    public AdminEntity mapToEntity() {
        AdminEntity ae = new AdminEntity();
        ae.setNickname(nickname);
        ae.setFullName(fullName);
        ae.setEmail(email);
        ae.setGender(gender);
        ae.setPhoneNumber(phoneNumber);
        ae.setCreatedAt(LocalDateTime.now());
        ae.setAvatar(FileEntity.builder().id(avatarId).url(avatarUrl).build());
        ae.setAddress(address);
        ae.setBirthDate(LocalDate.parse(birthday));
        ae.setPassword(password);
        ae.setEnabled(enabled);
        return ae;
    }
}
