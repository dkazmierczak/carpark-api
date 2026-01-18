package com.carpark.repository;

import com.carpark.model.ParkingSpace;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

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
}
