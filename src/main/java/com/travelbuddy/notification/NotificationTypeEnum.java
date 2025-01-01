package com.travelbuddy.notification;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum NotificationTypeEnum {
    SITE_COMMENT(1),
    SITE_REACTION(2),
    REVIEW_REACTION(3),
    PLAN(4),
    PLAN_ADD(5),
    PLAN_UPDATE(6),
    PLAN_DELETE(7),
    SITE_BAN(11),
    SITE_UNBAN(12),
    REVIEW_BAN(13),
    REVIEW_UNBAN(14),
    USER_BAN(15),
    USER_UNBAN(16);

    private final int type;

    public static NotificationTypeEnum valueOf(int type) {
        for (NotificationTypeEnum value : values()) {
            if (value.type == type) {
                return value;
            }
        }
        throw new IllegalArgumentException("Invalid notification type: " + type);
    }
}