package com.xdra.hub.service;

import com.xdra.hub.entity.EisMeasurementEntity;
import com.xdra.hub.entity.GeneratedRecordEntity;
import com.xdra.hub.entity.PackMetricsRecordEntity;
import com.xdra.hub.model.EisMeasurement;
import com.xdra.hub.model.GeneratedRecord;
import com.xdra.hub.model.PackMetricsRecord;
import com.xdra.hub.model.TransmitDataRequest;
import com.xdra.hub.repository.EisMeasurementRepository;
import com.xdra.hub.repository.GeneratedRecordRepository;
import com.xdra.hub.repository.PackMetricsRecordRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class TransmissionService {

    private final EisMeasurementRepository eisMeasurementRepository;
    private final GeneratedRecordRepository generatedRecordRepository;
    private final PackMetricsRecordRepository packMetricsRecordRepository;

    public void transmitData(TransmitDataRequest request) {
        log.info("received request to transmit data {} eis measurements, {} generated records, and {} pack metrics records",
                request.getEisMeasurements().size(), request.getGeneratedRecords().size(), request.getPackMetricsRecords().size());
        if (!CollectionUtils.isEmpty(request.getEisMeasurements())) {
            eisMeasurementRepository.saveAll(request.getEisMeasurements().stream().map(dto -> EisMeasurementEntity.builder()
                            .containerId(Long.parseLong(dto.getContainerId()))
                            .clusterId(Long.parseLong(dto.getClusterId()))
                            .packId(Long.parseLong(dto.getPackId()))
                            .cellId(Long.parseLong(dto.getCellId()))
                            .realImpedance(dto.getRealImpedance())
                            .imaginaryImpedance(dto.getImaginaryImpedance())
                            .temperature(dto.getTemperature())
                            .frequency(dto.getFrequency())
                            .voltage(dto.getVoltage())
                            .creationTime(dto.getCreationTime().toInstant())
                            .build())
                    .collect(Collectors.toList()));
        }
        if (!CollectionUtils.isEmpty(request.getGeneratedRecords())) {
            generatedRecordRepository.saveAll(request.getGeneratedRecords().stream().map(dto ->
                    GeneratedRecordEntity.builder()
                            .containerId(Long.parseLong(dto.getContainerId()))
                            .clusterId(Long.parseLong(dto.getClusterId()))
                            .packId(Long.parseLong(dto.getPackId()))
                            .cellId(Long.parseLong(dto.getCellId()))
                            .creationTime(dto.getCreationTime().toInstant())
                            .dispersionCoefficient(dto.getDispersionCoefficient())
                            .temperature(dto.getTemperature())
                            .seiParameter(dto.getSeiParameter())
                            .dendritesParameter(dto.getDendritesParameter())
                            .electrolyteParameter(dto.getElectrolyteParameter())
                            .polarizationPotential(dto.getPolarizationPotential())
                            .conductivity(dto.getConductivity())
                            .build()).collect(Collectors.toList()));
        }
        if (!CollectionUtils.isEmpty(request.getPackMetricsRecords())) {
            packMetricsRecordRepository.saveAll(request.getPackMetricsRecords().stream().map(dto ->
                    PackMetricsRecordEntity.builder()
                            .containerId(Long.parseLong(dto.getContainerId()))
                            .clusterId(Long.parseLong(dto.getClusterId()))
                            .packId(Long.parseLong(dto.getPackId()))
                            .creationTime(dto.getCreationTime().toInstant())
                            .dispersionCoefficient(dto.getDispersionCoefficient())
                            .safetyRate(dto.getSafetyRate())
                            .build()).collect(Collectors.toList()));
        }
    }

    public static void main(String[] args) {
        RestTemplate restTemplate = new RestTemplateBuilder()
                .setConnectTimeout(Duration.ofSeconds(5))
                .setReadTimeout(Duration.ofMinutes(1))
                .build();
        TransmitDataRequest request = new TransmitDataRequest();
        List<EisMeasurement> measurements = new ArrayList<>(7 * 2 * 8 * 52 * 3);
        OffsetDateTime creationTime = OffsetDateTime.now();
        Random random = new Random();
        List<Double> frequencies = List.of(1000.0, 2000.0, 3000.0);
        // 3 days
        for (int a = 0; a < 3; a++) {
            creationTime = creationTime.plusDays(1);
            // 2 clusters
            for (int b = 0; b < 2; b++) {
                // 8 packs
                for (int c = 0; c < 8; c++) {
                    // 52 cells
                    for (int d = 0; d < 52; d++) {
                        for (double frequency : frequencies) {
                            EisMeasurement measurement = new EisMeasurement();
                            measurement.setContainerId(String.valueOf(b + 1));
                            measurement.setClusterId(String.valueOf(b + 1));
                            measurement.setPackId(String.valueOf(8 * b + c + 1));
                            measurement.setCellId(String.valueOf(d + 1));
                            measurement.setTemperature(random.nextDouble(10, 60));
                            measurement.setVoltage(random.nextDouble(1, 10));
                            measurement.setFrequency(frequency);
                            measurement.setRealImpedance(random.nextDouble(100, 200));
                            measurement.setImaginaryImpedance(random.nextDouble(1, 10));
                            measurement.setCreationTime(creationTime);
                            measurements.add(measurement);
                        }
                    }
                }
            }
        }
        request.setEisMeasurements(measurements);

        creationTime = OffsetDateTime.now();
        List<GeneratedRecord> generatedRecords = new ArrayList<>(2 * 8 * 52);
        // 3 days
        for (int a = 0; a < 3; a++) {// 2 clusters
            creationTime = creationTime.plusDays(1);
            for (int b = 0; b < 2; b++) {
                // 8 packs
                for (int c = 0; c < 8; c++) {
                    // 52 cells
                    for (int d = 0; d < 52; d++) {
                        GeneratedRecord record = new GeneratedRecord();
                        record.setContainerId(String.valueOf(b + 1));
                        record.setClusterId(String.valueOf(b + 1));
                        record.setPackId(String.valueOf(8 * b + c + 1));
                        record.setCellId(String.valueOf(d + 1));
                        record.setTemperature(random.nextDouble(10, 60));
                        record.setDispersionCoefficient(random.nextDouble(0.1, 0.9));
                        record.setSeiParameter(random.nextInt(1, 10));
                        record.setDendritesParameter(random.nextInt(1, 10));
                        record.setElectrolyteParameter(random.nextInt(1, 10));
                        record.setPolarizationPotential(random.nextDouble(0.1, 0.9));
                        record.setConductivity(random.nextDouble(0.1, 0.9));
                        record.setCreationTime(creationTime);
                        generatedRecords.add(record);
                    }
                }
            }
        }
        request.setGeneratedRecords(generatedRecords);

        creationTime = OffsetDateTime.now();
        List<PackMetricsRecord> packMetricsRecords = new ArrayList<>(2 * 8);
        // 3 days
        for (int a = 0; a < 3; a++) {
            creationTime = creationTime.plusDays(1);
            // 2 clusters
            for (int b = 0; b < 2; b++) {
                // 8 packs
                for (int c = 0; c < 8; c++) {
                    PackMetricsRecord record = new PackMetricsRecord();
                    record.setContainerId(String.valueOf(b + 1));
                    record.setClusterId(String.valueOf(b + 1));
                    record.setPackId(String.valueOf(8 * b + c + 1));
                    record.setDispersionCoefficient(random.nextDouble(0.1, 0.9));
                    record.setSafetyRate(random.nextDouble(0.1, 0.9));
                    record.setCreationTime(creationTime);
                    packMetricsRecords.add(record);
                }
            }
        }
        request.setPackMetricsRecords(packMetricsRecords);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<TransmitDataRequest> requestEntity = new HttpEntity<>(request, headers);
        ResponseEntity<Void> response = restTemplate.postForEntity("http://localhost:8081/api/v1/transmit-data", requestEntity, Void.class);
        System.out.println(response.getStatusCode());
    }
}
