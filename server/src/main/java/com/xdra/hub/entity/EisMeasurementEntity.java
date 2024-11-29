package com.xdra.hub.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Table(name = "eis_measurement")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EisMeasurementEntity {
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
    @Column(name = "cell_id")
    private Long cellId;
    @Column(name = "real_impedance")
    private Double realImpedance;
    @Column(name = "imaginary_impedance")
    private Double imaginaryImpedance;
    @Column(name = "temperature")
    private Double temperature;
    @Column(name = "frequency")
    private Double frequency;
    @Column(name = "voltage")
    private Double voltage;
    @Column(name = "creation_time")
    private Instant creationTime;
}
