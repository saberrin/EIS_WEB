package com.xdra.hub.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Table(name = "generated_record")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GeneratedRecordEntity {
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
    @Column(name = "creation_time")
    private Instant creationTime;
    @Column(name = "dispersion_coefficient")
    private Double dispersionCoefficient;
    @Column(name = "temperature")
    private Double temperature;
    @Column(name = "sei_parameter")
    private Integer seiParameter;
    @Column(name = "dendrites_parameter")
    private Integer dendritesParameter;
    @Column(name = "electrolyte_parameter")
    private Integer electrolyteParameter;
    @Column(name = "polarization_potential")
    private Double polarizationPotential;
    @Column(name = "conductivity")
    private Double conductivity;
}
