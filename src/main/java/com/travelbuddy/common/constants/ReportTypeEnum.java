package com.travelbuddy.common.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ReportTypeEnum {
    SITE(1),
    SITE_REVIEW(2),
    USER(3);

    private final int type;
}
