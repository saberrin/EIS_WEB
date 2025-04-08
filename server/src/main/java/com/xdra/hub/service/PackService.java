package com.xdra.hub.service;

import com.xdra.hub.configuration.ConfigurationService;
import com.xdra.hub.repository.EisMeasurementRepository;
import com.xdra.hub.repository.GeneratedRecordRepository;
import com.xdra.hub.repository.PackMetricsRecordRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class PackService {

    private final EisMeasurementRepository eisMeasurementRepository;
    private final GeneratedRecordRepository generatedRecordRepository;
    private final PackMetricsRecordRepository packMetricsRecordRepository;
    private final ConfigurationService configurationService;

    @Value("${dashboard.number-pack-metrics:7}")
    private int numberOfPackMetrics;

    @Value("${dashboard.data-retention-days:30}")
    private int dataRetentionDays;
}
