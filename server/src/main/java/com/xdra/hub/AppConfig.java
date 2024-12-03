package com.xdra.hub;

import com.github.kagkarlsson.scheduler.Scheduler;
import com.github.kagkarlsson.scheduler.task.helper.RecurringTask;
import com.github.kagkarlsson.scheduler.task.helper.Tasks;
import com.github.kagkarlsson.scheduler.task.schedule.FixedDelay;
import com.xdra.hub.repository.DataRetentionManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class AppConfig {

    @Bean(initMethod = "start", destroyMethod = "stop")
    public Scheduler scheduler(DataSource dataSource, DataRetentionManager dataRetentionManager) {
        RecurringTask<Void> hourlyTask = Tasks.recurring("create-partitions-task", FixedDelay.ofHours(24))
                .execute((inst, ctx) -> {
                    dataRetentionManager.createTablePartitions();
                });

        return Scheduler
                .create(dataSource)
                .startTasks(hourlyTask)
                .threads(5)
                .build();
    }
}
