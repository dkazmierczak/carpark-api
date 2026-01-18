package com.carpark.model;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ParkingSpace {
    private final int spaceNumber;
    private String vehicleReg;
    private VehicleType vehicleType;
    private LocalDateTime timeIn;
    private boolean occupied;

    public ParkingSpace(int spaceNumber) {
        this.spaceNumber = spaceNumber;
        this.occupied = false;
    }

    public void park(String vehicleReg, VehicleType vehicleType, LocalDateTime timeIn) {
        this.vehicleReg = vehicleReg;
        this.vehicleType = vehicleType;
        this.timeIn = timeIn;
        this.occupied = true;
    }
}
