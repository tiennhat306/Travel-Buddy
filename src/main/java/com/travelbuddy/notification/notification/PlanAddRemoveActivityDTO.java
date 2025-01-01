package com.travelbuddy.notification.notification;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlanAddRemoveActivityDTO {
    private Integer userId;
    private Integer siteId;
}