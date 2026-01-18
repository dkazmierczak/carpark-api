package com.carpark.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ParkVehicleRequest {
    
    @NotBlank(message = "Vehicle registration is required")
    private String vehicleReg;
    
    @NotNull(message = "Vehicle type is required")
    @Min(value = 1, message = "Vehicle type must be 1, 2, or 3")
    @Max(value = 3, message = "Vehicle type must be 1, 2, or 3")
    private Integer vehicleType;
}
