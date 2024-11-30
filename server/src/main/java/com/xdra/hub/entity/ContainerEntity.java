package com.xdra.hub.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "container")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContainerEntity {
    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    private String name;

    @OneToMany(mappedBy = "container", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<ClusterEntity> clusters;
}
