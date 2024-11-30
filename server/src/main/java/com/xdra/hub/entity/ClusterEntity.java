package com.xdra.hub.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "cluster")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClusterEntity {
    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    private String name;

    @ManyToOne
    @JoinColumn(name = "container_id")
    private ContainerEntity container;

    @OneToMany(mappedBy = "cluster", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<PackEntity> packs;
}
