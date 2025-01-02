package com.travelbuddy.persistence.domain.dto.account.admin;

import com.travelbuddy.persistence.domain.entity.AdminEntity;
import com.travelbuddy.persistence.domain.entity.GroupEntity;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Data
@Builder
@Getter
@Setter
public class AdminRspdDto {
    private Integer id;
    private String nickname;
    private String fullName;
    private String email;
    private String gender;
    private String phoneNumber;
    private String avatar;
    private String createdAt;
    private Set<GroupEntity> groups;
    private boolean isEnabled;
    private String address;

//    public AdminRspdDto(AdminEntity adminEntity) {
//        this.id = adminEntity.getId();
//        this.nickname = adminEntity.getNickname();
//        this.fullName = adminEntity.getFullName();
//        this.email = adminEntity.getEmail();
//        this.gender = adminEntity.getGender();
//        this.phoneNumber = adminEntity.getPhoneNumber();
//        this.avatar = adminEntity.getAvatar().getUrl();
//        this.createdAt = adminEntity.getCreatedAt().toString();
//        this.groups = adminEntity.getGroupEntities();
//    }
}
