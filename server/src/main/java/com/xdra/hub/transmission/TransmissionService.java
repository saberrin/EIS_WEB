package com.xdra.hub.transmission;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xdra.hub.entity.CellStatisticsEntity;
import com.xdra.hub.entity.EisMeasurementEntity;
import com.xdra.hub.entity.PackStatisticsEntity;
import com.xdra.hub.model.EisMeasurement;
import com.xdra.hub.model.TransmitDataRequest;
import com.xdra.hub.repository.*;
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
    private final CellStatisticsRepository cellStatisticsRepository;
    private final PackStatisticsRepository packStatisticsRepository;
    private final ObjectMapper objectMapper;

    public void transmitData(TransmitDataRequest request) {
        if (!CollectionUtils.isEmpty(request.getEisMeasurements())) {
            log.info("received request to transmit data {} eis measurements",
                    request.getEisMeasurements().size());
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
        if (!CollectionUtils.isEmpty(request.getCellStatistics())) {
            log.info("received {} cell statistics records", request.getCellStatistics().size());
            cellStatisticsRepository.saveAll(request.getCellStatistics().stream().map(dto -> CellStatisticsEntity.builder()
                            .containerId(Long.parseLong(dto.getContainerId()))
                            .clusterId(Long.parseLong(dto.getClusterId()))
                            .packId(Long.parseLong(dto.getPackId()))
                            .groupId(Long.parseLong(dto.getGroupId()))
                            .cellId(Long.parseLong(dto.getCellId()))
                            .absImpedanceMean(dto.getAbsImpedanceMean())
                            .impedanceStdDev(dto.getImpedanceStdDev())
                            .coefficientOfVariation(dto.getCoefficientOfVariation())
                            .phase(dto.getPhase())
                            .impMeanToMaxRatio(dto.getImpedanceMeanToMaxRatio())
                            .impMeanToMinRatio(dto.getImpedanceMeanToMinRatio())
                            .impMeanToAvgRatio(dto.getImpedanceMeanToAvgRatio())
                            .impStdDevToMaxRatio(dto.getImpedanceStdDevToMaxRatio())
                            .impStdDevToMinRatio(dto.getImpedanceStdDevToMinRatio())
                            .impStdDevToAvgRatio(dto.getImpedanceStdDevToAvgRatio())
                            .nyquistPlot(objectMapper.valueToTree(dto.getNyquistPlot()))
                            .bodePlot(objectMapper.valueToTree(dto.getBodePlot()))
                            .drtPlot(objectMapper.valueToTree(dto.getDrtPlot()))
                            .impedanceMeanMatrix(objectMapper.valueToTree(dto.getImpedanceMeanMatrix()))
                            .impedanceStdDevMatrix(objectMapper.valueToTree(dto.getImpedanceStdDevMatrix()))
                            .eqCircuitData(objectMapper.valueToTree(dto.getEquivalentCircuitDiagram()))
                            .creationTime(dto.getCreationTime().toInstant())
                            .build())
                    .collect(Collectors.toList()));
        }
        if (!CollectionUtils.isEmpty(request.getPackStatistics())) {
            log.info("received {} pack statistics records", request.getPackStatistics().size());
            packStatisticsRepository.saveAll(request.getPackStatistics().stream().map(dto -> PackStatisticsEntity.builder()
                            .containerId(Long.parseLong(dto.getContainerId()))
                            .clusterId(Long.parseLong(dto.getClusterId()))
                            .packId(Long.parseLong(dto.getPackId()))
                            .absImpedanceMean(dto.getAbsImpedanceMean())
                            .impedanceStdDev(dto.getImpedanceStdDev())
                            .coefficientOfVariation(dto.getCoefficientOfVariation())
                            .dispersion(dto.getDispersion())
                            .characteristicFrequencies(objectMapper.valueToTree(dto.getCharacteristicFrequencies()))
                            .maxAbsImpedance(dto.getMaxAbsImpedance())
                            .minAbsImpedance(dto.getMinAbsImpedance())
                            .maxCoefficientOfVariation(dto.getMaxCoefficientOfVariation())
                            .minCoefficientOfVariation(dto.getMinCoefficientOfVariation())
                            .maxImpedanceStdDev(dto.getMaxImpedanceStdDev())
                            .minImpedanceStdDev(dto.getMinImpedanceStdDev())
                            .temperature(dto.getTemperature())
                            .degradationLevel(dto.getDegradationLevel())
                            .suggestion(dto.getSuggestion())
                            .alertText(dto.getAlertText())
                            .creationTime(dto.getCreationTime().toInstant())
                            .build())
                    .collect(Collectors.toList()));
        }
    }

    public static void main(String[] args) {
        RestTemplate restTemplate = new RestTemplateBuilder()
                .setConnectTimeout(Duration.ofSeconds(5))
                .setReadTimeout(Duration.ofMinutes(1))
                .build();
        TransmitDataRequest request = new TransmitDataRequest();
        List<EisMeasurement> measurements = new ArrayList<>(7 * 2 * 8 * 52 * 3);
        OffsetDateTime creationTime = OffsetDateTime.now().minusDays(3);
        Random random = new Random();
        List<Double> frequencies = List.of(1000.0, 2000.0, 3000.0);
        // 3 days
        for (int a = 0; a < 3; a++) {
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

            creationTime = creationTime.plusDays(1);
        }
        request.setEisMeasurements(measurements);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<TransmitDataRequest> requestEntity = new HttpEntity<>(request, headers);
        ResponseEntity<Void> response = restTemplate.postForEntity("http://localhost:8080/api/v1/transmit-data", requestEntity, Void.class);
        System.out.println(response.getStatusCode());
    }
}
