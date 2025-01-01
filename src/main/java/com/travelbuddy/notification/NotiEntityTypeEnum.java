package com.travelbuddy.notification;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum NotiEntityTypeEnum {
    SITE(1),
    SITE_REVIEW(2),
    USER(3),
    TRAVEL_PLAN(4);

    private final int type;
}