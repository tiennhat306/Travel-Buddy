package com.travelbuddy.mapper;

import com.travelbuddy.persistence.domain.dto.account.admin.AdminDetailRspnDto;
import com.travelbuddy.persistence.domain.dto.account.admin.AdminRspdDto;
import com.travelbuddy.persistence.domain.dto.account.user.UserDetailRspnDto;
import com.travelbuddy.persistence.domain.entity.AdminEntity;
import com.travelbuddy.persistence.domain.dto.auth.BasicInfoDto;
import com.travelbuddy.persistence.domain.entity.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AdminMapper {
    @Mapping(target = "id", source = "id")
    @Mapping(target = "email", source = "email")
    @Mapping(target = "nickname", source = "nickname")
    @Mapping(target = "fullName", source = "fullName")
    @Mapping(target = "avatar", source = "avatar.url")
    BasicInfoDto toBasicInfoDto(AdminEntity adminEntity);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "nickname", source = "nickname")
    @Mapping(target = "fullName", source = "fullName")
    @Mapping(target = "email", source = "email")
    @Mapping(target = "gender", source = "gender")
    @Mapping(target = "avatar", source = "avatar.url")
    @Mapping(target = "phoneNumber", source = "phoneNumber")
    @Mapping(target = "createdAt", expression = "java(adminEntity.getCreatedAt().toString())")
    AdminDetailRspnDto toAdminDetailRspnDto(AdminEntity adminEntity);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "nickname", source = "nickname")
    @Mapping(target = "fullName", source = "fullName")
    @Mapping(target = "email", source = "email")
    @Mapping(target = "gender", source = "gender")
    @Mapping(target = "avatar", source = "avatar.url")
    @Mapping(target = "phoneNumber", source = "phoneNumber")
    @Mapping(target = "createdAt", expression = "java(adminEntity.getCreatedAt().toString())")
    @Mapping(target = "groups", source = "groupEntities")
    @Mapping(target = "isEnabled", source = "enabled")
    @Mapping(target = "address", source = "address")
    AdminRspdDto toAdminRspdDto(AdminEntity adminEntity);
}
