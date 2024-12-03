package com.xdra.hub.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataRetentionManager {

    private final DataSource dataSource;

    public void createTablePartitions() {
        log.info("preparing table partitions");

        DateTimeFormatter dayFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter monthFormatter = DateTimeFormatter.ofPattern("yyyy_MM");
        ZonedDateTime now = ZonedDateTime.now().truncatedTo(ChronoUnit.DAYS).withDayOfMonth(1);
        List<String> tableNames = List.of("eis_measurement", "generated_record", "pack_metrics_record");
        List<ZonedDateTime> dates = List.of(now, now.plusMonths(1));

        for (String tableName : tableNames) {
            for (ZonedDateTime date : dates) {
                String partitionName = tableName + "_" + monthFormatter.format(date);

                String createPartitionQuery = String.format(
                        "CREATE TABLE IF NOT EXISTS %s PARTITION OF %s FOR VALUES FROM ('%s') TO ('%s')",
                        partitionName, tableName, dayFormatter.format(date), dayFormatter.format(date.plusMonths(1))
                );
                String createPartitionIndexQuery = String.format(
                        "CREATE INDEX IF NOT EXISTS idx_%s ON %s (pack_id, creation_time)",
                        partitionName, partitionName
                );

                try (Connection connection = dataSource.getConnection()) {
                    connection.prepareStatement(createPartitionQuery).execute();
                    connection.prepareStatement(createPartitionIndexQuery).execute();
                } catch (Throwable e) {
                    log.error("failed to execute query date {} table {}", date, tableName, e);
                }
            }
        }

        log.info("table partitions created");
    }
}
