package com.xdra.hub.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "pack")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PackEntity {
    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    private String name;

    @ManyToOne
    @JoinColumn(name = "cluster_id")
    private ClusterEntity cluster;
}
