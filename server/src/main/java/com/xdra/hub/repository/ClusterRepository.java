package com.xdra.hub.repository;

import com.xdra.hub.entity.ClusterEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClusterRepository extends JpaRepository<ClusterEntity, Long> {
}
