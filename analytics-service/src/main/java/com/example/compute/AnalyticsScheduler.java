package com.example.compute;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AnalyticsScheduler {

    private final AnalyticsComputation analyticsComputation;

    @Scheduled(cron = "0 */15 * * * ?")
    public void scheduledAnalyticsComputation() {
        analyticsComputation.computeTaskStatistics();
        analyticsComputation.computeUserPerformance();
        analyticsComputation.computeProjectProgress();
    }
}
