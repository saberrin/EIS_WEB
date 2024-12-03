package com.xdra.hub.controller;

import com.xdra.hub.api.ConfigurationApi;
import com.xdra.hub.model.SystemConfiguration;
import com.xdra.hub.configuration.ConfigurationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class ConfigurationController implements ConfigurationApi {

    private final ConfigurationService configurationService;

    @Override
    public ResponseEntity<SystemConfiguration> getSystemConfiguration() {
        return new ResponseEntity<>(configurationService.getSystemConfiguration(), HttpStatus.OK);
    }
}
