# Quick Start Guide

This guide will help you get the Car Park API running in under 5 minutes.

## Prerequisites
- Java 17+ installed
- Maven 3.6+ installed

## Step 1: Build the Project
```bash
cd carpark-api
mvn clean install
```

## Step 2: Run the Application
```bash
mvn spring-boot:run
```

Wait for the message: "Started CarParkApplication in X seconds"

## Step 3: Test the API

### Option A: Using curl

**Check parking status:**
```bash
curl http://localhost:8080/parking
```

**Park a vehicle:**
```bash
curl -X POST http://localhost:8080/parking \
  -H "Content-Type: application/json" \
  -d '{"vehicleReg":"ABC123","vehicleType":1}'
```

**Generate bill:**
```bash
curl -X POST http://localhost:8080/parking/bill \
  -H "Content-Type: application/json" \
  -d '{"vehicleReg":"ABC123"}'
```

### Option B: Using Postman
1. Import `CarParkAPI.postman_collection.json`
2. Run the requests in order

## API Summary

| Endpoint | Method | Description |
|----------|--------|-------------|
| `/parking` | GET | Get parking status |
| `/parking` | POST | Park a vehicle |
| `/parking/bill` | POST | Generate bill and exit |
