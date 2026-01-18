package com.carpark.service;

import com.carpark.dto.*;
import com.carpark.model.ParkingSpace;
import com.carpark.model.VehicleType;
import com.carpark.repository.ParkingRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;


@Service
public class ParkingService {

    private final ParkingRepository parkingRepository;

    public ParkingService(ParkingRepository parkingRepository) {
        this.parkingRepository = parkingRepository;
    }

    public ParkingStatusResponse getParkingStatus() {
        int available = (int) parkingRepository.countAvailableSpaces();
        int occupied = (int) parkingRepository.countOccupiedSpaces();
        return new ParkingStatusResponse(available, occupied);
    }

    public ParkVehicleResponse parkVehicle(ParkVehicleRequest request) {
        if (parkingRepository.isVehicleParked(request.getVehicleReg())) {
            // TODO exception
            throw new RuntimeException();
        }

        ParkingSpace space = parkingRepository.findFirstAvailableSpace()
                //TODO exception
                .orElseThrow(RuntimeException::new);

        VehicleType vehicleType = VehicleType.fromCode(request.getVehicleType());
        LocalDateTime timeIn = LocalDateTime.now();
        space.park(request.getVehicleReg(), vehicleType, timeIn);

        return new ParkVehicleResponse(
                request.getVehicleReg(),
                space.getSpaceNumber(),
                timeIn
        );
    }
}
