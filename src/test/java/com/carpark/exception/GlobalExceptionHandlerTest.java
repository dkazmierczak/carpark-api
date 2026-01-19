package com.carpark.exception;

import com.carpark.controller.ParkingController;
import com.carpark.dto.BillRequest;
import com.carpark.dto.ParkVehicleRequest;
import com.carpark.service.ParkingService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ParkingController.class)
@Import(GlobalExceptionHandler.class)
class GlobalExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ParkingService parkingService;

    @Test
    void handleCarParkFull_ShouldReturn409Conflict() throws Exception {
        // Given
        ParkVehicleRequest request = new ParkVehicleRequest("ABC123", 1);
        when(parkingService.parkVehicle(any()))
                .thenThrow(new CarParkFullException("No available parking spaces"));

        // When & Then
        mockMvc.perform(post("/parking")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.message").value("No available parking spaces"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void handleVehicleNotFound_ShouldReturn404NotFound() throws Exception {
        // Given
        BillRequest request = new BillRequest("NOTFOUND");
        when(parkingService.generateBillAndExit(any()))
                .thenThrow(new VehicleNotFoundException("Vehicle NOTFOUND not found in car park"));

        // When & Then
        mockMvc.perform(post("/parking/bill")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Vehicle NOTFOUND not found in car park"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void handleVehicleAlreadyParked_ShouldReturn409Conflict() throws Exception {
        // Given
        ParkVehicleRequest request = new ParkVehicleRequest("ABC123", 1);
        when(parkingService.parkVehicle(any()))
                .thenThrow(new VehicleAlreadyParkedException("Vehicle ABC123 is already parked"));

        // When & Then
        mockMvc.perform(post("/parking")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.message").value("Vehicle ABC123 is already parked"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void handleValidationException_ShouldReturn400_WhenVehicleRegBlank() throws Exception {
        // Given - blank vehicleReg
        ParkVehicleRequest request = new ParkVehicleRequest("", 1);

        // When & Then
        mockMvc.perform(post("/parking")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.vehicleReg").exists())
                .andExpect(jsonPath("$.vehicleReg").value(containsString("required")));
    }

    @Test
    void handleValidationException_ShouldReturn400_WhenVehicleRegNull() throws Exception {
        // Given - null vehicleReg
        ParkVehicleRequest request = new ParkVehicleRequest(null, 1);

        // When & Then
        mockMvc.perform(post("/parking")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.vehicleReg").exists());
    }

    @Test
    void handleValidationException_ShouldReturn400_WhenVehicleTypeNull() throws Exception {
        // Given - null vehicleType
        ParkVehicleRequest request = new ParkVehicleRequest("ABC123", null);

        // When & Then
        mockMvc.perform(post("/parking")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.vehicleType").exists());
    }

    @Test
    void handleValidationException_ShouldReturn400_WhenVehicleTypeTooSmall() throws Exception {
        // Given - vehicleType < 1
        ParkVehicleRequest request = new ParkVehicleRequest("ABC123", 0);

        // When & Then
        mockMvc.perform(post("/parking")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.vehicleType").exists())
                .andExpect(jsonPath("$.vehicleType").value(containsString("1, 2, or 3")));
    }

    @Test
    void handleValidationException_ShouldReturn400_WhenVehicleTypeTooLarge() throws Exception {
        // Given - vehicleType > 3
        ParkVehicleRequest request = new ParkVehicleRequest("ABC123", 4);

        // When & Then
        mockMvc.perform(post("/parking")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.vehicleType").exists())
                .andExpect(jsonPath("$.vehicleType").value(containsString("1, 2, or 3")));
    }

    @Test
    void handleValidationException_ShouldReturn400_WhenMultipleFieldsInvalid() throws Exception {
        // Given - both fields invalid
        ParkVehicleRequest request = new ParkVehicleRequest("", 99);

        // When & Then
        mockMvc.perform(post("/parking")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.vehicleReg").exists())
                .andExpect(jsonPath("$.vehicleType").exists());
    }

    @Test
    void handleGenericException_ShouldReturn500InternalServerError() throws Exception {
        // Given
        ParkVehicleRequest request = new ParkVehicleRequest("ABC123", 1);
        when(parkingService.parkVehicle(any()))
                .thenThrow(new RuntimeException("Unexpected database error"));

        // When & Then
        mockMvc.perform(post("/parking")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.message").value("An unexpected error occurred: Unexpected database error"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void handleGenericException_ShouldCatchNullPointerException() throws Exception {
        // Given
        ParkVehicleRequest request = new ParkVehicleRequest("ABC123", 1);
        when(parkingService.parkVehicle(any()))
                .thenThrow(new NullPointerException("Something went null"));

        // When & Then
        mockMvc.perform(post("/parking")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.message").value(containsString("unexpected error")))
                .andExpect(jsonPath("$.timestamp").exists());
    }
}
