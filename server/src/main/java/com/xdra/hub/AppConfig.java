package com.xdra.hub;

import com.github.kagkarlsson.scheduler.Scheduler;
import com.github.kagkarlsson.scheduler.task.helper.RecurringTask;
import com.github.kagkarlsson.scheduler.task.helper.Tasks;
import com.github.kagkarlsson.scheduler.task.schedule.FixedDelay;
import com.xdra.hub.analytics.AnalyticsService;
import com.xdra.hub.repository.DataRetentionManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class AppConfig {

    @Bean(initMethod = "start", destroyMethod = "stop")
    public Scheduler scheduler(DataSource dataSource,
                               DataRetentionManager dataRetentionManager,
                               AnalyticsService analyticsService) {
        RecurringTask<Void> createTablePartitionTask = Tasks.recurring("create-partitions-task", FixedDelay.ofHours(24))
                .execute((inst, ctx) -> dataRetentionManager.createTablePartitions());
        RecurringTask<Void> monthlyStatsTask = Tasks.recurring("monthly-stats-task", FixedDelay.ofHours(24))
                .execute((inst, ctx) -> analyticsService.calculateMonthlyStats());

        return Scheduler
                .create(dataSource)
                .startTasks(createTablePartitionTask, monthlyStatsTask)
                .threads(5)
                .build();
    }
}
