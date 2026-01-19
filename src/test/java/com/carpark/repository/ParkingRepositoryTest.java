package com.carpark.repository;

import com.carpark.model.ParkingSpace;
import com.carpark.model.VehicleType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class ParkingRepositoryTest {

    private ParkingRepository parkingRepository;

    @BeforeEach
    void setUp() {
        parkingRepository = new ParkingRepository();
    }

    @Test
    void constructor_ShouldInitialize50Spaces() {
        // When - repository is created in @BeforeEach

        // Then
        assertEquals(50, parkingRepository.countAvailableSpaces());
        assertEquals(0, parkingRepository.countOccupiedSpaces());
    }

    @Test
    void constructor_ShouldInitializeSpacesWithSequentialNumbers() {
        // When
        Optional<ParkingSpace> firstSpace = parkingRepository.findFirstAvailableSpace();

        // Then
        assertTrue(firstSpace.isPresent());
        assertEquals(1, firstSpace.get().getSpaceNumber());
    }

    @Test
    void constructor_ShouldInitializeAllSpacesAsUnoccupied() {
        // When
        for (int i = 1; i <= 50; i++) {
            Optional<ParkingSpace> space = parkingRepository.findFirstAvailableSpace();

            // Then
            assertTrue(space.isPresent());
            assertFalse(space.get().isOccupied());

            // Mark as occupied for next iteration
            space.get().park("TEST" + i, VehicleType.SMALL, LocalDateTime.now());
        }

        // All spaces should be occupied now
        assertEquals(50, parkingRepository.countOccupiedSpaces());
        assertEquals(0, parkingRepository.countAvailableSpaces());
    }

    @Test
    void findFirstAvailableSpace_ShouldReturnFirstSpace_WhenAllAvailable() {
        // When
        Optional<ParkingSpace> space = parkingRepository.findFirstAvailableSpace();

        // Then
        assertTrue(space.isPresent());
        assertEquals(1, space.get().getSpaceNumber());
        assertFalse(space.get().isOccupied());
    }

    @Test
    void findFirstAvailableSpace_ShouldReturnNextAvailable_WhenFirstIsOccupied() {
        // Given - occupy first space
        ParkingSpace firstSpace = parkingRepository.findFirstAvailableSpace().get();
        firstSpace.park("ABC123", VehicleType.SMALL, LocalDateTime.now());

        // When
        Optional<ParkingSpace> nextSpace = parkingRepository.findFirstAvailableSpace();

        // Then
        assertTrue(nextSpace.isPresent());
        assertEquals(2, nextSpace.get().getSpaceNumber());
    }

    @Test
    void findFirstAvailableSpace_ShouldReturnEmpty_WhenAllSpacesOccupied() {
        // Given - occupy all spaces
        for (int i = 1; i <= 50; i++) {
            ParkingSpace space = parkingRepository.findFirstAvailableSpace().get();
            space.park("CAR" + i, VehicleType.SMALL, LocalDateTime.now());
        }

        // When
        Optional<ParkingSpace> space = parkingRepository.findFirstAvailableSpace();

        // Then
        assertFalse(space.isPresent());
    }

    @Test
    void findFirstAvailableSpace_ShouldReturnVacatedSpace_AfterVehicleExits() {
        // Given - park 3 vehicles
        ParkingSpace space1 = parkingRepository.findFirstAvailableSpace().get();
        space1.park("CAR1", VehicleType.SMALL, LocalDateTime.now());

        ParkingSpace space2 = parkingRepository.findFirstAvailableSpace().get();
        space2.park("CAR2", VehicleType.MEDIUM, LocalDateTime.now());

        ParkingSpace space3 = parkingRepository.findFirstAvailableSpace().get();
        space3.park("CAR3", VehicleType.LARGE, LocalDateTime.now());

        // When - CAR1 exits (space 1 becomes available)
        space1.vacate();
        Optional<ParkingSpace> availableSpace = parkingRepository.findFirstAvailableSpace();

        // Then - space 1 should be returned (first available)
        assertTrue(availableSpace.isPresent());
        assertEquals(1, availableSpace.get().getSpaceNumber());
    }

    @Test
    void findByVehicleReg_ShouldReturnSpace_WhenVehicleIsParked() {
        // Given
        ParkingSpace space = parkingRepository.findFirstAvailableSpace().get();
        space.park("ABC123", VehicleType.SMALL, LocalDateTime.now());

        // When
        Optional<ParkingSpace> found = parkingRepository.findByVehicleReg("ABC123");

        // Then
        assertTrue(found.isPresent());
        assertEquals("ABC123", found.get().getVehicleReg());
        assertEquals(1, found.get().getSpaceNumber());
    }

    @Test
    void findByVehicleReg_ShouldReturnEmpty_WhenVehicleNotParked() {
        // When
        Optional<ParkingSpace> found = parkingRepository.findByVehicleReg("NOTFOUND");

        // Then
        assertFalse(found.isPresent());
    }

    @Test
    void findByVehicleReg_ShouldBeCaseInsensitive() {
        // Given
        ParkingSpace space = parkingRepository.findFirstAvailableSpace().get();
        space.park("ABC123", VehicleType.SMALL, LocalDateTime.now());

        // When & Then - all variations should work
        assertTrue(parkingRepository.findByVehicleReg("ABC123").isPresent());
        assertTrue(parkingRepository.findByVehicleReg("abc123").isPresent());
        assertTrue(parkingRepository.findByVehicleReg("AbC123").isPresent());
        assertTrue(parkingRepository.findByVehicleReg("ABC123").isPresent());
    }

    @Test
    void findByVehicleReg_ShouldReturnCorrectSpace_WhenMultipleVehiclesParked() {
        // Given - park multiple vehicles
        ParkingSpace space1 = parkingRepository.findFirstAvailableSpace().get();
        space1.park("CAR001", VehicleType.SMALL, LocalDateTime.now());

        ParkingSpace space2 = parkingRepository.findFirstAvailableSpace().get();
        space2.park("CAR002", VehicleType.MEDIUM, LocalDateTime.now());

        ParkingSpace space3 = parkingRepository.findFirstAvailableSpace().get();
        space3.park("CAR003", VehicleType.LARGE, LocalDateTime.now());

        // When
        Optional<ParkingSpace> foundSpace2 = parkingRepository.findByVehicleReg("CAR002");

        // Then
        assertTrue(foundSpace2.isPresent());
        assertEquals("CAR002", foundSpace2.get().getVehicleReg());
        assertEquals(2, foundSpace2.get().getSpaceNumber());
    }

    @Test
    void countAvailableSpaces_ShouldReturn50_WhenNoneOccupied() {
        // When
        long count = parkingRepository.countAvailableSpaces();

        // Then
        assertEquals(50, count);
    }

    @Test
    void countAvailableSpaces_ShouldDecrease_WhenVehiclesParked() {
        // Given
        ParkingSpace space1 = parkingRepository.findFirstAvailableSpace().get();
        space1.park("CAR1", VehicleType.SMALL, LocalDateTime.now());

        ParkingSpace space2 = parkingRepository.findFirstAvailableSpace().get();
        space2.park("CAR2", VehicleType.SMALL, LocalDateTime.now());

        // When
        long count = parkingRepository.countAvailableSpaces();

        // Then
        assertEquals(48, count);
    }

    @Test
    void countAvailableSpaces_ShouldReturn0_WhenAllOccupied() {
        // Given - occupy all spaces
        for (int i = 1; i <= 50; i++) {
            ParkingSpace space = parkingRepository.findFirstAvailableSpace().get();
            space.park("CAR" + i, VehicleType.SMALL, LocalDateTime.now());
        }

        // When
        long count = parkingRepository.countAvailableSpaces();

        // Then
        assertEquals(0, count);
    }

    @Test
    void countAvailableSpaces_ShouldIncrease_WhenVehicleExits() {
        // Given
        ParkingSpace space1 = parkingRepository.findFirstAvailableSpace().get();
        space1.park("CAR1", VehicleType.SMALL, LocalDateTime.now());

        ParkingSpace space2 = parkingRepository.findFirstAvailableSpace().get();
        space2.park("CAR2", VehicleType.SMALL, LocalDateTime.now());

        assertEquals(48, parkingRepository.countAvailableSpaces());

        // When - one vehicle exits
        space1.vacate();

        // Then
        assertEquals(49, parkingRepository.countAvailableSpaces());
    }

    @Test
    void countOccupiedSpaces_ShouldReturn0_WhenNoneOccupied() {
        // When
        long count = parkingRepository.countOccupiedSpaces();

        // Then
        assertEquals(0, count);
    }

    @Test
    void countOccupiedSpaces_ShouldIncrease_WhenVehiclesParked() {
        // Given
        ParkingSpace space1 = parkingRepository.findFirstAvailableSpace().get();
        space1.park("CAR1", VehicleType.SMALL, LocalDateTime.now());

        ParkingSpace space2 = parkingRepository.findFirstAvailableSpace().get();
        space2.park("CAR2", VehicleType.SMALL, LocalDateTime.now());

        // When
        long count = parkingRepository.countOccupiedSpaces();

        // Then
        assertEquals(2, count);
    }

    @Test
    void countOccupiedSpaces_ShouldReturn50_WhenAllOccupied() {
        // Given - occupy all spaces
        for (int i = 1; i <= 50; i++) {
            ParkingSpace space = parkingRepository.findFirstAvailableSpace().get();
            space.park("CAR" + i, VehicleType.SMALL, LocalDateTime.now());
        }

        // When
        long count = parkingRepository.countOccupiedSpaces();

        // Then
        assertEquals(50, count);
    }

    @Test
    void countOccupiedSpaces_ShouldDecrease_WhenVehicleExits() {
        // Given
        ParkingSpace space1 = parkingRepository.findFirstAvailableSpace().get();
        space1.park("CAR1", VehicleType.SMALL, LocalDateTime.now());

        ParkingSpace space2 = parkingRepository.findFirstAvailableSpace().get();
        space2.park("CAR2", VehicleType.SMALL, LocalDateTime.now());

        assertEquals(2, parkingRepository.countOccupiedSpaces());

        // When - one vehicle exits
        space1.vacate();

        // Then
        assertEquals(1, parkingRepository.countOccupiedSpaces());
    }

    @Test
    void isVehicleParked_ShouldReturnTrue_WhenVehicleIsParked() {
        // Given
        ParkingSpace space = parkingRepository.findFirstAvailableSpace().get();
        space.park("ABC123", VehicleType.SMALL, LocalDateTime.now());

        // When
        boolean isParked = parkingRepository.isVehicleParked("ABC123");

        // Then
        assertTrue(isParked);
    }

    @Test
    void isVehicleParked_ShouldReturnFalse_WhenVehicleNotParked() {
        // When
        boolean isParked = parkingRepository.isVehicleParked("NOTFOUND");

        // Then
        assertFalse(isParked);
    }

    @Test
    void isVehicleParked_ShouldBeCaseInsensitive() {
        // Given
        ParkingSpace space = parkingRepository.findFirstAvailableSpace().get();
        space.park("ABC123", VehicleType.SMALL, LocalDateTime.now());

        // When & Then - all variations should return true
        assertTrue(parkingRepository.isVehicleParked("ABC123"));
        assertTrue(parkingRepository.isVehicleParked("abc123"));
        assertTrue(parkingRepository.isVehicleParked("AbC123"));
        assertTrue(parkingRepository.isVehicleParked("ABC123"));
    }

    @Test
    void isVehicleParked_ShouldReturnFalse_AfterVehicleExits() {
        // Given
        ParkingSpace space = parkingRepository.findFirstAvailableSpace().get();
        space.park("ABC123", VehicleType.SMALL, LocalDateTime.now());

        assertTrue(parkingRepository.isVehicleParked("ABC123"));

        // When
        space.vacate();

        // Then
        assertFalse(parkingRepository.isVehicleParked("ABC123"));
    }

    @Test
    void availableAndOccupiedCounts_ShouldAlwaysSum50() {
        // Initially
        assertEquals(50, parkingRepository.countAvailableSpaces() +
                parkingRepository.countOccupiedSpaces());

        // After parking 10 vehicles
        for (int i = 1; i <= 10; i++) {
            ParkingSpace space = parkingRepository.findFirstAvailableSpace().get();
            space.park("CAR" + i, VehicleType.SMALL, LocalDateTime.now());
        }
        assertEquals(50, parkingRepository.countAvailableSpaces() +
                parkingRepository.countOccupiedSpaces());

        // After some vehicles exit
        parkingRepository.findByVehicleReg("CAR1").get().vacate();
        parkingRepository.findByVehicleReg("CAR5").get().vacate();
        assertEquals(50, parkingRepository.countAvailableSpaces() +
                parkingRepository.countOccupiedSpaces());

        // After all exit
        for (int i = 2; i <= 10; i++) {
            if (i != 5) { // already vacated
                parkingRepository.findByVehicleReg("CAR" + i)
                        .ifPresent(ParkingSpace::vacate);
            }
        }
        assertEquals(50, parkingRepository.countAvailableSpaces() +
                parkingRepository.countOccupiedSpaces());
    }

    @Test
    void fullParkingCycle_ShouldWorkCorrectly() {
        // 1. Park 50 vehicles (fill the car park)
        for (int i = 1; i <= 50; i++) {
            Optional<ParkingSpace> space = parkingRepository.findFirstAvailableSpace();
            assertTrue(space.isPresent());
            space.get().park("CAR" + i, VehicleType.SMALL, LocalDateTime.now());
        }

        // Verify full
        assertEquals(0, parkingRepository.countAvailableSpaces());
        assertEquals(50, parkingRepository.countOccupiedSpaces());
        assertFalse(parkingRepository.findFirstAvailableSpace().isPresent());

        // 2. Free up some spaces
        parkingRepository.findByVehicleReg("CAR10").get().vacate();
        parkingRepository.findByVehicleReg("CAR25").get().vacate();
        parkingRepository.findByVehicleReg("CAR40").get().vacate();

        // Verify partial availability
        assertEquals(3, parkingRepository.countAvailableSpaces());
        assertEquals(47, parkingRepository.countOccupiedSpaces());

        // 3. Park new vehicles in freed spaces
        ParkingSpace newSpace1 = parkingRepository.findFirstAvailableSpace().get();
        assertEquals(10, newSpace1.getSpaceNumber()); // First vacated space
        newSpace1.park("NEWCAR1", VehicleType.MEDIUM, LocalDateTime.now());

        ParkingSpace newSpace2 = parkingRepository.findFirstAvailableSpace().get();
        assertEquals(25, newSpace2.getSpaceNumber());
        newSpace2.park("NEWCAR2", VehicleType.LARGE, LocalDateTime.now());

        // Verify
        assertEquals(1, parkingRepository.countAvailableSpaces());
        assertEquals(49, parkingRepository.countOccupiedSpaces());
    }
}