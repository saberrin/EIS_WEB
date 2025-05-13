package com.xdra.hub.service;

import com.xdra.hub.repository.EisMeasurementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.List;

import com.xdra.hub.entity.EisMeasurementEntity;

@Service
@RequiredArgsConstructor
public class DataExportService {

    private final EisMeasurementRepository eisMeasurementRepository;

    /**
     * Streams CSV data for EisMeasurements between two instants to the provided OutputStream.
     */
    public void exportData(Instant start, Instant end, OutputStream out) {
        List<EisMeasurementEntity> measurements = eisMeasurementRepository.findByCreationTimeBetween(start, end);
        try (PrintWriter writer = new PrintWriter(new OutputStreamWriter(out, StandardCharsets.UTF_8))) {
            // Write CSV header
            writer.println("id,containerId,clusterId,packId,cellId,realImpedance,imaginaryImpedance,temperature,frequency,voltage,creationTime");
            // Write each measurement as a CSV row
            for (EisMeasurementEntity m : measurements) {
                writer.printf("%d,%d,%d,%d,%s,%s,%s,%s,%s,%s\n",
                        m.getContainerId(),
                        m.getClusterId(),
                        m.getPackId(),
                        m.getCellId(),
                        m.getRealImpedance() != null ? m.getRealImpedance().toString() : "",
                        m.getImaginaryImpedance() != null ? m.getImaginaryImpedance().toString() : "",
                        m.getTemperature() != null ? m.getTemperature().toString() : "",
                        m.getFrequency() != null ? m.getFrequency().toString() : "",
                        m.getVoltage() != null ? m.getVoltage().toString() : "",
                        m.getCreationTime() != null ? m.getCreationTime().toString() : ""
                );
            }
            writer.flush();
        } catch (Exception e) {
            throw new RuntimeException("Failed to export data as CSV", e);
        }
    }
}
