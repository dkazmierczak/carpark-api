package com.carpark.repository;

import com.carpark.model.ParkingSpace;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class ParkingRepository {
    
    private final List<ParkingSpace> parkingSpaces;
    private static final int TOTAL_SPACES = 50;

    public ParkingRepository() {
        this.parkingSpaces = new ArrayList<>();
        for (int i = 1; i <= TOTAL_SPACES; i++) {
            parkingSpaces.add(new ParkingSpace(i));
        }
    }

    public long countAvailableSpaces() {
        return parkingSpaces.stream()
                .filter(space -> !space.isOccupied())
                .count();
    }

    public long countOccupiedSpaces() {
        return parkingSpaces.stream()
                .filter(ParkingSpace::isOccupied)
                .count();
    }

    public boolean isVehicleParked(String vehicleReg) {
        return parkingSpaces.stream()
                .anyMatch(space -> space.isOccupied() &&
                        space.getVehicleReg().equalsIgnoreCase(vehicleReg));
    }

    public Optional<ParkingSpace> findFirstAvailableSpace() {
        return parkingSpaces.stream()
                .filter(space -> !space.isOccupied())
                .findFirst();
    }

    public Optional<ParkingSpace> findByVehicleReg(String vehicleReg) {
        return parkingSpaces.stream()
                .filter(space -> space.isOccupied() &&
                        space.getVehicleReg().equalsIgnoreCase(vehicleReg))
                .findFirst();
    }
}
