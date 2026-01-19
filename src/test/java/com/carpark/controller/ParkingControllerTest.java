package com.carpark.controller;

import com.carpark.dto.*;
import com.carpark.service.ParkingService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ParkingController.class)
class ParkingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ParkingService parkingService;

    @Test
    void getParkingStatus_ShouldReturnStatus() throws Exception {
        // Given
        ParkingStatusResponse statusResponse = new ParkingStatusResponse(45, 5);
        when(parkingService.getParkingStatus()).thenReturn(statusResponse);

        // When & Then
        mockMvc.perform(get("/parking"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.availableSpaces").value(45))
                .andExpect(jsonPath("$.occupiedSpaces").value(5));
    }

    @Test
    void parkVehicle_ShouldReturnCreated_WhenSuccessful() throws Exception {
        // Given
        ParkVehicleRequest request = new ParkVehicleRequest("ABC123", 1);
        ParkVehicleResponse response = new ParkVehicleResponse(
            "ABC123", 1, LocalDateTime.now()
        );
        when(parkingService.parkVehicle(any(ParkVehicleRequest.class)))
                .thenReturn(response);

        // When & Then
        mockMvc.perform(post("/parking")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.vehicleReg").value("ABC123"))
                .andExpect(jsonPath("$.spaceNumber").value(1))
                .andExpect(jsonPath("$.timeIn").exists());
    }

    @Test
    void parkVehicle_ShouldReturnBadRequest_WhenVehicleRegMissing() throws Exception {
        // Given
        ParkVehicleRequest request = new ParkVehicleRequest(null, 1);

        // When & Then
        mockMvc.perform(post("/parking")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void parkVehicle_ShouldReturnBadRequest_WhenInvalidVehicleType() throws Exception {
        // Given
        ParkVehicleRequest request = new ParkVehicleRequest("ABC123", 5);

        // When & Then
        mockMvc.perform(post("/parking")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void generateBill_ShouldReturnBill_WhenSuccessful() throws Exception {
        // Given
        BillRequest request = new BillRequest("ABC123");
        BillResponse response = new BillResponse(
            "bill-123",
            "ABC123",
            5.50,
            LocalDateTime.now().minusMinutes(10),
            LocalDateTime.now()
        );
        when(parkingService.generateBillAndExit(any(BillRequest.class)))
                .thenReturn(response);

        // When & Then
        mockMvc.perform(post("/parking/bill")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.billId").value("bill-123"))
                .andExpect(jsonPath("$.vehicleReg").value("ABC123"))
                .andExpect(jsonPath("$.vehicleCharge").value(5.50))
                .andExpect(jsonPath("$.timeIn").exists())
                .andExpect(jsonPath("$.timeOut").exists());
    }

    @Test
    void generateBill_ShouldReturnBadRequest_WhenVehicleRegMissing() throws Exception {
        // Given
        BillRequest request = new BillRequest(null);

        // When & Then
        mockMvc.perform(post("/parking/bill")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}
