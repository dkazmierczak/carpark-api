package com.carpark.controller;

import com.carpark.dto.*;
import com.carpark.service.ParkingService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
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

    /**
     * POST /parking
     * Parks a given vehicle in the first available space
     */
    @PostMapping
    public ResponseEntity<ParkVehicleResponse> parkVehicle(
            @Valid @RequestBody ParkVehicleRequest request) {
        ParkVehicleResponse response = parkingService.parkVehicle(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * POST /parking/bill
     * Frees up vehicle's space and returns final charge
     */
    @PostMapping("/bill")
    public ResponseEntity<BillResponse> generateBill(
            @Valid @RequestBody BillRequest request) {
        BillResponse response = parkingService.generateBillAndExit(request);
        return ResponseEntity.ok(response);
    }
}
