package com.petmanagement.petserve.dto.health;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class VetDailyStatsResponse {
    private Integer pendingCount;
    private Integer confirmedCount;
    private Integer completedCount;
    private Integer cancelledCount;
}
