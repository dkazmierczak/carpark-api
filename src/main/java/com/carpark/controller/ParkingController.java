package com.carpark.controller;

import com.carpark.dto.*;
import com.carpark.service.ParkingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/parking")
public class ParkingController {

    private final ParkingService parkingService;

    public ParkingController(ParkingService parkingService) {
        this.parkingService = parkingService;
    }

    /**
     * GET /parking
     * Gets available and occupied number of spaces
     */
    @GetMapping
    public ResponseEntity<ParkingStatusResponse> getParkingStatus() {
        ParkingStatusResponse status = parkingService.getParkingStatus();
        return ResponseEntity.ok(status);
    }
}
