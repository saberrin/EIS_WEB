package com.xdra.hub.controller;

import com.xdra.hub.api.TransmissionApi;
import com.xdra.hub.model.TransmitDataRequest;
import com.xdra.hub.transmission.TransmissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class TransmissionController implements TransmissionApi {

    private final TransmissionService transmissionService;

    @Override
    public ResponseEntity<Void> transmitData(TransmitDataRequest transmitDataRequest) {
        transmissionService.transmitData(transmitDataRequest);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
}
