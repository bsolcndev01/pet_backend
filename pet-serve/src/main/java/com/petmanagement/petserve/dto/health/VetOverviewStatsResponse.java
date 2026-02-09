package com.petmanagement.petserve.dto.health;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class VetOverviewStatsResponse {
    private PeriodStats today;
    private PeriodStats week;
    private PeriodStats month;

    @Data
    @Builder
    public static class PeriodStats {
        private Integer appointments;
        private Integer patients;
        private Integer medications;
    }
}
