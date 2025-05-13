package com.xdra.hub.entity;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;

@Entity
@Table(name = "pack_statistics")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PackStatisticsEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "container_id")
    private Long containerId;

    @Column(name = "cluster_id")
    private Long clusterId;

    @Column(name = "pack_id")
    private Long packId;

    @Column(name = "abs_impedance_mean")
    private Double absImpedanceMean;

    @Column(name = "impedance_std_dev")
    private Double impedanceStdDev;

    @Column(name = "coefficient_variation")
    private Double coefficientOfVariation;

    @Column(name = "dispersion")
    private Double dispersion;

    @Column(name = "characteristic_frequencies", columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    private JsonNode characteristicFrequencies;

    @Column(name = "max_abs_impedance")
    private Double maxAbsImpedance;

    @Column(name = "min_abs_impedance")
    private Double minAbsImpedance;

    @Column(name = "max_coefficient_variation")
    private Double maxCoefficientOfVariation;

    @Column(name = "min_coefficient_variation")
    private Double minCoefficientOfVariation;

    @Column(name = "max_impedance_std_dev")
    private Double maxImpedanceStdDev;

    @Column(name = "min_impedance_std_dev")
    private Double minImpedanceStdDev;

    @Column(name = "temperature")
    private Double temperature;

    @Column(name = "degradation_level")
    private String degradationLevel;

    @Column(name = "suggestion")
    private String suggestion;

    @Column(name = "alert_text")
    private String alertText;

    @Column(name = "creation_time")
    private Instant creationTime;

    @Column(name = "cell_colors")
    @JdbcTypeCode(SqlTypes.JSON)
    private JsonNode cellColors;
}
