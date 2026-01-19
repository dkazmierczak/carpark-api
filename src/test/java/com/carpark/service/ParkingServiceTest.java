package com.carpark.service;

import com.carpark.dto.*;
import com.carpark.exception.CarParkFullException;
import com.carpark.exception.VehicleAlreadyParkedException;
import com.carpark.exception.VehicleNotFoundException;
import com.carpark.model.ParkingSpace;
import com.carpark.model.VehicleType;
import com.carpark.repository.ParkingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ParkingServiceTest {

    @Mock
    private ParkingRepository parkingRepository;

    @InjectMocks
    private ParkingService parkingService;

    private ParkingSpace mockSpace;

    @BeforeEach
    void setUp() {
        mockSpace = new ParkingSpace(1);
    }

    @Test
    void getParkingStatus_ShouldReturnCorrectCounts() {
        // Given
        when(parkingRepository.countAvailableSpaces()).thenReturn(45L);
        when(parkingRepository.countOccupiedSpaces()).thenReturn(5L);

        // When
        ParkingStatusResponse response = parkingService.getParkingStatus();

        // Then
        assertEquals(45, response.getAvailableSpaces());
        assertEquals(5, response.getOccupiedSpaces());
        verify(parkingRepository).countAvailableSpaces();
        verify(parkingRepository).countOccupiedSpaces();
    }

    @Test
    void parkVehicle_ShouldParkSuccessfully_WhenSpaceAvailable() {
        // Given
        ParkVehicleRequest request = new ParkVehicleRequest("ABC123", 1);
        when(parkingRepository.isVehicleParked(anyString())).thenReturn(false);
        when(parkingRepository.findFirstAvailableSpace()).thenReturn(Optional.of(mockSpace));

        // When
        ParkVehicleResponse response = parkingService.parkVehicle(request);

        // Then
        assertNotNull(response);
        assertEquals("ABC123", response.getVehicleReg());
        assertEquals(1, response.getSpaceNumber());
        assertNotNull(response.getTimeIn());
        assertTrue(mockSpace.isOccupied());
        assertEquals("ABC123", mockSpace.getVehicleReg());
    }

    @Test
    void parkVehicle_ShouldThrowException_WhenVehicleAlreadyParked() {
        // Given
        ParkVehicleRequest request = new ParkVehicleRequest("ABC123", 1);
        when(parkingRepository.isVehicleParked("ABC123")).thenReturn(true);

        // When & Then
        assertThrows(VehicleAlreadyParkedException.class, 
            () -> parkingService.parkVehicle(request));
        verify(parkingRepository, never()).findFirstAvailableSpace();
    }

    @Test
    void parkVehicle_ShouldThrowException_WhenNoSpaceAvailable() {
        // Given
        ParkVehicleRequest request = new ParkVehicleRequest("ABC123", 1);
        when(parkingRepository.isVehicleParked(anyString())).thenReturn(false);
        when(parkingRepository.findFirstAvailableSpace()).thenReturn(Optional.empty());

        // When & Then
        assertThrows(CarParkFullException.class, 
            () -> parkingService.parkVehicle(request));
    }

    @Test
    void generateBillAndExit_ShouldCalculateCorrectly_ForSmallCar() {
        // Given
        LocalDateTime timeIn = LocalDateTime.now().minusMinutes(10);
        mockSpace.park("ABC123", VehicleType.SMALL, timeIn);
        BillRequest request = new BillRequest("ABC123");
        when(parkingRepository.findByVehicleReg("ABC123")).thenReturn(Optional.of(mockSpace));

        // When
        BillResponse response = parkingService.generateBillAndExit(request);

        // Then
        assertNotNull(response);
        assertEquals("ABC123", response.getVehicleReg());
        assertNotNull(response.getBillId());
        // 10 minutes * £0.10 = £1.00 + 2 blocks of 5 minutes * £1 = £3.00 total
        assertEquals(3.0, response.getVehicleCharge(), 0.01);
        assertFalse(mockSpace.isOccupied());
    }

    @Test
    void generateBillAndExit_ShouldCalculateCorrectly_ForMediumCar() {
        // Given
        LocalDateTime timeIn = LocalDateTime.now().minusMinutes(25);
        mockSpace.park("XYZ789", VehicleType.MEDIUM, timeIn);
        BillRequest request = new BillRequest("XYZ789");
        when(parkingRepository.findByVehicleReg("XYZ789")).thenReturn(Optional.of(mockSpace));

        // When
        BillResponse response = parkingService.generateBillAndExit(request);

        // Then
        assertNotNull(response);
        assertEquals("XYZ789", response.getVehicleReg());
        // 25 minutes * £0.20 = £5.00 + 5 blocks of 5 minutes * £1 = £10.00 total
        assertEquals(10.0, response.getVehicleCharge(), 0.01);
    }

    @Test
    void generateBillAndExit_ShouldCalculateCorrectly_ForLargeCar() {
        // Given
        LocalDateTime timeIn = LocalDateTime.now().minusMinutes(15);
        mockSpace.park("DEF456", VehicleType.LARGE, timeIn);
        BillRequest request = new BillRequest("DEF456");
        when(parkingRepository.findByVehicleReg("DEF456")).thenReturn(Optional.of(mockSpace));

        // When
        BillResponse response = parkingService.generateBillAndExit(request);

        // Then
        assertNotNull(response);
        assertEquals("DEF456", response.getVehicleReg());
        // 15 minutes * £0.40 = £6.00 + 3 blocks of 5 minutes * £1 = £9.00 total
        assertEquals(9.0, response.getVehicleCharge(), 0.01);
    }

    @Test
    void generateBillAndExit_ShouldThrowException_WhenVehicleNotFound() {
        // Given
        BillRequest request = new BillRequest("NOTFOUND");
        when(parkingRepository.findByVehicleReg("NOTFOUND")).thenReturn(Optional.empty());

        // When & Then
        assertThrows(VehicleNotFoundException.class, 
            () -> parkingService.generateBillAndExit(request));
    }

    @Test
    void generateBillAndExit_ShouldVacateSpace_AfterBilling() {
        // Given
        LocalDateTime timeIn = LocalDateTime.now().minusMinutes(5);
        mockSpace.park("ABC123", VehicleType.SMALL, timeIn);
        BillRequest request = new BillRequest("ABC123");
        when(parkingRepository.findByVehicleReg("ABC123")).thenReturn(Optional.of(mockSpace));

        // When
        parkingService.generateBillAndExit(request);

        // Then
        assertFalse(mockSpace.isOccupied());
        assertNull(mockSpace.getVehicleReg());
        assertNull(mockSpace.getTimeIn());
    }
}
