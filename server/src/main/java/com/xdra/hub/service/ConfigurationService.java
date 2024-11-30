package com.xdra.hub.service;

import com.xdra.hub.entity.ClusterEntity;
import com.xdra.hub.entity.ContainerEntity;
import com.xdra.hub.model.Cluster;
import com.xdra.hub.model.Container;
import com.xdra.hub.model.SystemConfiguration;
import com.xdra.hub.repository.ContainerRepository;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class ConfigurationService {

    private final ContainerRepository containerRepository;
    @Getter
    private volatile SystemConfiguration systemConfiguration;

    @PostConstruct
    public void init() {
        // refresh system configuration periodically
        Executors.newScheduledThreadPool(1).scheduleAtFixedRate(this::loadSystemConfiguration, 0, 3, TimeUnit.MINUTES);
    }

    private void loadSystemConfiguration() {
        SystemConfiguration systemConfiguration = new SystemConfiguration();
        List<ContainerEntity> containers = containerRepository.findAll();
        for (ContainerEntity container : containers) {
            Container containerModel = new Container();
            containerModel.setId(String.valueOf(container.getId()));

            for (ClusterEntity cluster : container.getClusters()) {
                Cluster clusterModel = new Cluster();
                clusterModel.setId(String.valueOf(cluster.getId()));
                clusterModel.setPacks(cluster.getPacks().stream().map(pack -> String.valueOf(pack.getId())).toList());
                containerModel.addClustersItem(clusterModel);
            }

            systemConfiguration.addContainersItem(containerModel);
        }
        this.systemConfiguration = systemConfiguration;
        log.info("system configuration loaded {}", systemConfiguration);
    }
}
