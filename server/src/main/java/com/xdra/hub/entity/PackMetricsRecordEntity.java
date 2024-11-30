package com.xdra.hub.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Table(name = "pack_metrics_record")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PackMetricsRecordEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name = "container_id")
    private Long containerId;
    @Column(name = "cluster_id")
    private Long clusterId;
    @Column(name = "pack_id")
    private Long packId;
    @Column(name = "dispersion_coefficient")
    private Double dispersionCoefficient;
    @Column(name = "safety_rate")
    private Double safetyRate;
    @Column(name = "creation_time")
    private Instant creationTime;
}
