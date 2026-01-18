package com.carpark.service;

import com.carpark.dto.*;
import com.carpark.exception.CarParkFullException;
import com.carpark.exception.VehicleAlreadyParkedException;
import com.carpark.exception.VehicleNotFoundException;
import com.carpark.model.ParkingSpace;
import com.carpark.model.VehicleType;
import com.carpark.repository.ParkingRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;


@Service
public class ParkingService {

    private final ParkingRepository parkingRepository;
    private static final double ADDITIONAL_CHARGE_PER_5_MINUTES = 1.0;

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
            throw new VehicleAlreadyParkedException(
                    "Vehicle " + request.getVehicleReg() + " is already parked"
            );
        }

        ParkingSpace space = parkingRepository.findFirstAvailableSpace()
                .orElseThrow(() -> new CarParkFullException("No available parking spaces"));

        VehicleType vehicleType = VehicleType.fromCode(request.getVehicleType());
        LocalDateTime timeIn = LocalDateTime.now();
        space.park(request.getVehicleReg(), vehicleType, timeIn);

        return new ParkVehicleResponse(
                request.getVehicleReg(),
                space.getSpaceNumber(),
                timeIn
        );
    }

    public BillResponse generateBillAndExit(BillRequest request) {
        ParkingSpace space = parkingRepository.findByVehicleReg(request.getVehicleReg())
                .orElseThrow(() -> new VehicleNotFoundException(
                        "Vehicle " + request.getVehicleReg() + " not found in car park"
                ));

        LocalDateTime timeOut = LocalDateTime.now();
        double charge = calculateCharge(space.getTimeIn(), timeOut, space.getVehicleType());

        BillResponse bill = new BillResponse(
                UUID.randomUUID().toString(),
                space.getVehicleReg(),
                charge,
                space.getTimeIn(),
                timeOut
        );

        space.vacate();

        return bill;
    }


    /**
     * Calculate parking charge based on:
     * - Per-minute rate based on vehicle type
     * - Additional £1 charge for every 5 minutes
     */
    private double calculateCharge(LocalDateTime timeIn, LocalDateTime timeOut, VehicleType vehicleType) {
        long totalMinutes = ChronoUnit.MINUTES.between(timeIn, timeOut);

        double baseCharge = totalMinutes * vehicleType.getRatePerMinute();

        long fiveMinuteBlocks = totalMinutes / 5;
        double additionalCharge = fiveMinuteBlocks * ADDITIONAL_CHARGE_PER_5_MINUTES;

        double totalCharge = baseCharge + additionalCharge;

        return Math.round(totalCharge * 100.0) / 100.0;
    }
}
