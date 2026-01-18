package com.carpark.service;

import com.carpark.dto.*;
import com.carpark.repository.ParkingRepository;
import org.springframework.stereotype.Service;


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
}
