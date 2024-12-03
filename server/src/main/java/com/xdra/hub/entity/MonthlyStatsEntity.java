package com.xdra.hub.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "monthly_statistics")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MonthlyStatsEntity {
    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "year_month")
    private String yearMonth;

    @Column(name = "total_inspections")
    private Integer totalInspections;

    @Column(name = "total_measurements")
    private Long totalMeasurements;
}
