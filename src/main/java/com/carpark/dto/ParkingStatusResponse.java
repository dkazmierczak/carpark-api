package com.carpark.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ParkingStatusResponse {
    private int availableSpaces;
    private int occupiedSpaces;
}
