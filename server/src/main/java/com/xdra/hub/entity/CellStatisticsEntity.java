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
@Table(name = "cell_statistics")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CellStatisticsEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "container_id")
    private Long containerId;

    @Column(name = "cluster_id")
    private Long clusterId;

    @Column(name = "pack_id")
    private Long packId;

    @Column(name = "group_id")
    private Long groupId;

    @Column(name = "cell_id")
    private Long cellId;

    @Column(name = "abs_impedance_mean")
    private Double absImpedanceMean;

    @Column(name = "impedance_std_dev")
    private Double impedanceStdDev;

    @Column(name = "coefficient_variation")
    private Double coefficientOfVariation;

    @Column(name = "phase")
    private Double phase;

    @Column(name = "imp_mean_to_max_ratio")
    private Double impMeanToMaxRatio;

    @Column(name = "imp_mean_to_min_ratio")
    private Double impMeanToMinRatio;

    @Column(name = "imp_mean_to_avg_ratio")
    private Double impMeanToAvgRatio;

    @Column(name = "imp_std_dev_to_max_ratio")
    private Double impStdDevToMaxRatio;

    @Column(name = "imp_std_dev_to_min_ratio")
    private Double impStdDevToMinRatio;

    @Column(name = "imp_std_dev_to_avg_ratio")
    private Double impStdDevToAvgRatio;

    @Column(name = "max_impedance_std_dev")
    private Double maxImpedanceStdDev;

    @Column(name = "min_impedance_std_dev")
    private Double minImpedanceStdDev;

    @Column(name = "max_abs_impedance")
    private Double maxAbsImpedance;

    @Column(name = "min_abs_impedance")
    private Double minAbsImpedance;

    @Column(name = "max_coefficient_variation")
    private Double maxCoefficientOfVariation;

    @Column(name = "min_coefficient_variation")
    private Double minCoefficientOfVariation;

    @Column(name = "nyquist_plot", columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    private JsonNode nyquistPlot;

    @Column(name = "bode_plot", columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    private JsonNode bodePlot;

    @Column(name = "drt_plot", columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    private JsonNode drtPlot;

    @Column(name = "imag_part_correlation", columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    private JsonNode imagPartCorrelation;

    @Column(name = "real_part_correlation", columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    private JsonNode realPartCorrelation;

    @Column(name = "equivalent_circuit_diagram", columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    private JsonNode equivalentCircuitData;

    @Column(name = "real_part_10hz")
    private Double realPart10Hz;

    @Column(name = "imaginary_part_10hz")
    private Double imaginaryPart10Hz;

    @Column(name = "creation_time")
    private Instant creationTime;
}
