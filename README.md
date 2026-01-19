# Car Park Management API

A simple REST API for managing a car park with 50 parking spaces. The API handles vehicle parking, space allocation, and billing calculations.

## Table of Contents
- [Features](#features)
- [Technologies](#technologies)
- [Prerequisites](#prerequisites)
- [Setup and Running](#setup-and-running)
- [API Endpoints](#api-endpoints)
- [Pricing Structure](#pricing-structure)
- [Testing](#testing)
- [Assumptions](#assumptions)
- [Questions for Clarification](#questions-for-clarification)

## Features

- ✅ Allocate vehicles to the first available parking space
- ✅ Check available and occupied parking spaces
- ✅ Calculate parking charges based on vehicle type and duration
- ✅ De-allocate spaces when vehicles exit
- ✅ In-memory data storage
- ✅ Comprehensive error handling
- ✅ Input validation
- ✅ Unit and integration tests

## Technologies

- **Java 17**
- **Spring Boot 3.2.0**
- **Maven 3.6+**
- **JUnit 5** for testing
- **Mockito** for mocking
- **Lombok** for reducing boilerplate code

## Prerequisites

Before running this application, ensure you have:

- Java 17 or higher installed
- Maven 3.6+ installed
- Your favorite IDE (IntelliJ IDEA, Eclipse, VS Code) or a terminal

To verify your installations:
```bash
java -version
mvn -version
```

## Setup and Running

### 1. Clone or Extract the Project

```bash
cd carpark-api
```

### 2. Build the Project

```bash
mvn clean install
```

This will:
- Compile the code
- Run all tests
- Package the application as a JAR file

### 3. Run the Application

```bash
mvn spring-boot:run
```

Or run the JAR directly:
```bash
java -jar target/carpark-api-1.0.0.jar
```

The application will start on **http://localhost:8080**

### 4. Verify It's Running

```bash
curl http://localhost:8080/parking
```

You should see:
```json
{
  "availableSpaces": 50,
  "occupiedSpaces": 0
}
```

## API Endpoints

### 1. Get Parking Status

**GET** `/parking`

Returns the number of available and occupied spaces.

**Response:**
```json
{
  "availableSpaces": 45,
  "occupiedSpaces": 5
}
```

**Example:**
```bash
curl http://localhost:8080/parking
```

---

### 2. Park a Vehicle

**POST** `/parking`

Parks a vehicle in the first available space.

**Request Body:**
```json
{
  "vehicleReg": "ABC123",
  "vehicleType": 1
}
```

**Vehicle Types:**
- `1` - Small Car (£0.10/minute)
- `2` - Medium Car (£0.20/minute)
- `3` - Large Car (£0.40/minute)

**Response:**
```json
{
  "vehicleReg": "ABC123",
  "spaceNumber": 1,
  "timeIn": "2024-01-18T10:30:00"
}
```

**Example:**
```bash
curl -X POST http://localhost:8080/parking \
  -H "Content-Type: application/json" \
  -d '{"vehicleReg":"ABC123","vehicleType":1}'
```

**Error Scenarios:**
- **409 Conflict** - Vehicle already parked or car park is full
- **400 Bad Request** - Invalid vehicle type or missing required fields

---

### 3. Generate Bill and Exit

**POST** `/parking/bill`

Calculates the final charge and frees up the parking space.

**Request Body:**
```json
{
  "vehicleReg": "ABC123"
}
```

**Response:**
```json
{
  "billId": "550e8400-e29b-41d4-a716-446655440000",
  "vehicleReg": "ABC123",
  "vehicleCharge": 3.00,
  "timeIn": "2024-01-18T10:30:00",
  "timeOut": "2024-01-18T10:40:00"
}
```

**Example:**
```bash
curl -X POST http://localhost:8080/parking/bill \
  -H "Content-Type: application/json" \
  -d '{"vehicleReg":"ABC123"}'
```

**Error Scenarios:**
- **404 Not Found** - Vehicle not found in the car park

---

## Pricing Structure

### Base Rates (per minute)
- Small Car: £0.10/minute
- Medium Car: £0.20/minute
- Large Car: £0.40/minute

### Additional Charges
- **Every 5 minutes**: £1.00 additional charge

### Calculation Example

For a **Small Car** parked for **10 minutes**:
- Base charge: 10 minutes × £0.10 = £1.00
- Additional charge: (10 ÷ 5) = 2 blocks × £1.00 = £2.00
- **Total: £3.00**

For a **Medium Car** parked for **25 minutes**:
- Base charge: 25 minutes × £0.20 = £5.00
- Additional charge: (25 ÷ 5) = 5 blocks × £1.00 = £5.00
- **Total: £10.00**

For a **Large Car** parked for **15 minutes**:
- Base charge: 15 minutes × £0.40 = £6.00
- Additional charge: (15 ÷ 5) = 3 blocks × £1.00 = £3.00
- **Total: £9.00**

## Testing

### Run All Tests

```bash
mvn test
```

### Test Coverage

The project includes:

**Unit Tests** (`ParkingServiceTest.java`):
- ✅ Get parking status
- ✅ Park vehicle successfully
- ✅ Handle duplicate parking attempts
- ✅ Handle car park full scenario
- ✅ Calculate charges correctly for all vehicle types
- ✅ Handle vehicle not found
- ✅ Verify space vacated after billing

**Integration Tests** (`ParkingControllerTest.java`):
- ✅ API endpoint responses
- ✅ Request validation
- ✅ Error handling
- ✅ JSON serialization/deserialization

### Run Tests with Coverage (if you have coverage tools)

```bash
mvn test jacoco:report
```

## Assumptions

### 1. **Fixed Number of Spaces**
- The car park has exactly **50 parking spaces**
- This is configured in `ParkingRepository.java` and can be easily changed

### 2. **First Available Space Allocation**
- Vehicles are allocated to the first available space (lowest numbered space)
- No preference or reservation system

### 3. **Unique Vehicle Registration**
- Each vehicle has a unique registration number
- A vehicle can only be parked once at a time
- Attempting to park a vehicle that's already parked will result in an error

### 4. **Time Calculation**
- Parking duration is calculated in minutes
- The system uses `LocalDateTime` for time tracking
- No time zone considerations (uses system time)

### 5. **Charge Calculation**
- The additional £1 charge applies for **every complete 5-minute block**
- For example: 9 minutes = 1 block, 10 minutes = 2 blocks
- Charges are rounded to 2 decimal places

### 6. **In-Memory Storage**
- All data is stored in memory using Java collections
- Data is lost when the application restarts
- No database or file persistence

### 7. **Single Instance**
- The application is designed to run as a single instance
- No consideration for distributed systems or concurrent access from multiple instances

### 8. **Case Insensitivity**
- Vehicle registration numbers are case-insensitive
- "ABC123" and "abc123" are treated as the same vehicle

### 9. **No Authentication/Authorization**
- The API is open and does not require authentication
- Suitable for internal use or demo purposes

### 10. **Minimum Parking Duration**
- Even if a vehicle is parked for less than 1 minute, no charge is applied
- The charge starts accumulating from the first minute

## Questions for Clarification

If this were a real-world project, I would ask:

### Business Logic Questions
1. **Charge Rounding**: Should charges be rounded up, down, or to nearest? Currently rounding to 2 decimal places.
2. **Minimum Charge**: Is there a minimum charge even for very short stays (< 1 minute)?
3. **Maximum Duration**: Is there a maximum parking duration? Should we handle overnight parking?
4. **Reserved Spaces**: Are there any reserved spaces (disabled, VIP, electric vehicle charging)?
5. **Payment**: How is payment handled? Should we integrate with a payment gateway?

### Technical Questions
1. **Concurrency**: How should we handle concurrent requests? Should we implement locking mechanisms?
2. **Persistence**: What's the plan for data persistence? Database? File system? Redis?
3. **Scalability**: Do we need to support multiple car park locations? Multiple floors?
4. **Monitoring**: What metrics should we track? Occupancy rates, revenue, peak hours?
5. **API Versioning**: Should we version the API for future changes?

### Operational Questions
1. **Error Recovery**: What happens if the system crashes while vehicles are parked?
2. **Audit Trail**: Do we need to maintain a history of all transactions?
3. **Reporting**: What kind of reports are needed (daily revenue, occupancy trends)?
4. **Notifications**: Should we notify users when the car park is nearly full?
5. **Time Zones**: If the car park serves multiple time zones, how should we handle it?

## Project Structure

```
carpark-api/
├── src/
│   ├── main/
│   │   ├── java/com/carpark/
│   │   │   ├── controller/
│   │   │   │   └── ParkingController.java
│   │   │   ├── service/
│   │   │   │   └── ParkingService.java
│   │   │   ├── repository/
│   │   │   │   └── ParkingRepository.java
│   │   │   ├── model/
│   │   │   │   ├── ParkingSpace.java
│   │   │   │   └── VehicleType.java
│   │   │   ├── dto/
│   │   │   │   ├── ParkingStatusResponse.java
│   │   │   │   ├── ParkVehicleRequest.java
│   │   │   │   ├── ParkVehicleResponse.java
│   │   │   │   ├── BillRequest.java
│   │   │   │   └── BillResponse.java
│   │   │   ├── exception/
│   │   │   │   ├── CarParkFullException.java
│   │   │   │   ├── VehicleNotFoundException.java
│   │   │   │   ├── VehicleAlreadyParkedException.java
│   │   │   │   └── GlobalExceptionHandler.java
│   │   │   └── CarParkApplication.java
│   │   └── resources/
│   │       └── application.properties
│   └── test/
│       └── java/com/carpark/
│           ├── service/
│           │   └── ParkingServiceTest.java
│           └── controller/
│               └── ParkingControllerTest.java
├── pom.xml
├── .gitignore
├── PROJECT_SUMMARY.md
├── QUICKSTART.md
├── README.md
└── README_Java.md
```

## Additional Features (Not Implemented)

Given more time, the following enhancements could be added:

1. **Database Integration** - PostgreSQL or H2 for persistence
2. **Docker Support** - Containerize the application
3. **Swagger/OpenAPI** - Interactive API documentation
4. **Metrics & Monitoring** - Actuator endpoints, Prometheus integration
5. **Logging** - Structured logging with correlation IDs
6. **Rate Limiting** - Prevent API abuse
7. **Caching** - Redis for improved performance
8. **Admin Endpoints** - Reset car park, view all parked vehicles
9. **Vehicle Search** - Find vehicles by partial registration
10. **Historical Data** - Track all parking sessions and revenue
