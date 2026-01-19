# Car Park API - Project Summary

## Overview
A complete Spring Boot REST API for car park management with 50 spaces, implementing all required endpoints and business logic.

## ✅ Requirements Met

### Functional Requirements
- ✅ Allocates vehicles to first available space
- ✅ Tracks available and occupied spaces
- ✅ Calculates parking charges on vehicle exit
- ✅ De-allocates space on vehicle exit
- ✅ Charges per minute based on vehicle type
- ✅ Additional £1 charge every 5 minutes
- ✅ Supports 3 vehicle types (Small, Medium, Large)

### Technical Requirements
- ✅ In-memory data storage (no disk persistence)
- ✅ Comprehensive error handling with custom exceptions
- ✅ Unit tests for service layer (ParkingServiceTest)
- ✅ Integration tests for controller layer (ParkingControllerTest)
- ✅ All 3 API endpoints implemented exactly as specified

## 🏗️ Architecture

### Layered Architecture
```
Controller Layer (REST API)
    ↓
Service Layer (Business Logic)
    ↓
Repository Layer (Data Access)
    ↓
In-Memory Storage (List<ParkingSpace>)
```

### Key Components

**Models**
- `ParkingSpace` - Represents a parking space with occupancy status
- `VehicleType` - Enum for vehicle types with rates

**DTOs (Data Transfer Objects)**
- Request/Response objects for clean API contracts
- Input validation using Jakarta Bean Validation

**Exceptions**
- `CarParkFullException` - No available spaces
- `VehicleNotFoundException` - Vehicle not found when billing
- `VehicleAlreadyParkedException` - Duplicate parking attempt
- `GlobalExceptionHandler` - Centralized exception handling

**Repository**
- In-memory storage with 50 parking spaces
- Thread-safe operations using Java Streams

**Service**
- Business logic for parking operations
- Charge calculation algorithm
- Space allocation logic

**Controller**
- REST endpoints
- Request validation
- HTTP status codes

## 💡 Key Design Decisions

### 1. Enum for Vehicle Types
```java
public enum VehicleType {
    SMALL(1, 0.10),
    MEDIUM(2, 0.20),
    LARGE(3, 0.40);
}
```
**Benefit**: Type-safe, easy to extend, encapsulates rate information

### 2. Immutable DTOs with Validation
```java
@NotBlank(message = "Vehicle registration is required")
private String vehicleReg;
```
**Benefit**: Clear API contract, automatic validation, better error messages

### 3. Centralized Exception Handling
```java
@RestControllerAdvice
public class GlobalExceptionHandler { ... }
```
**Benefit**: Consistent error responses, cleaner controller code

### 4. First Available Space Algorithm
```java
parkingSpaces.stream()
    .filter(space -> !space.isOccupied())
    .findFirst();
```
**Benefit**: Simple, efficient O(n) lookup, easy to understand

### 5. Precise Charge Calculation
```java
double baseCharge = totalMinutes * vehicleType.getRatePerMinute();
long fiveMinuteBlocks = totalMinutes / 5;
double additionalCharge = fiveMinuteBlocks * ADDITIONAL_CHARGE_PER_5_MINUTES;
```
**Benefit**: Clear, testable, matches requirements exactly

## 📊 Charge Calculation Examples

| Vehicle | Duration | Base Charge | Additional | Total |
|---------|----------|-------------|------------|-------|
| Small   | 10 min   | £1.00       | £2.00      | £3.00 |
| Medium  | 25 min   | £5.00       | £5.00      | £10.00|
| Large   | 15 min   | £6.00       | £3.00      | £9.00 |

---

**Technology Stack**: Java 17, Spring Boot 3.2.0, Maven, JUnit 5, Mockito, Lombok
