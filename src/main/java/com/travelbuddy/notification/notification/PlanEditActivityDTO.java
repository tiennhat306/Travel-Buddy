package com.travelbuddy.notification.notification;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlanEditActivityDTO {
    private Integer userId;
    private String from;
    private String to;
}