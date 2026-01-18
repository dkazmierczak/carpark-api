# Java Take Home Task Instructions

## Project Overview:
- Your task is to create a simple car park management API. Please read this document fully and carefully to ensure you don’t miss any requirements.
- You should create and structure the project in the way you feel most appropriate.
- You have the freedom to come up with your own solution entirely, our only ask is that it must cater for the requirements and define the endpoints you will find in the table below.
- Whilst this is a take home task, and we hope you use it as an opportunity to showcase your abilities, we also understand that your time is valuable. Please don’t feel compelled to over engineer the solution or spend many hours on it, we suggest 2-3 hours.

## Requirements:
- Your API should be able to handle the following scenarios:
- Allocating vehicles to the first available space
- Determine the number of available and full spaces
- Determine the parking charge on vehicle exit
- De-Allocate a space on vehicle exit
- Vehicles will be charged per minute they are parked
- The parking charges are:
    - Small Car - £0.10/minute (1)
    - Medium Car - £0.20/minute (2)
    - Large Car £0.40/minute (3)
- Every 5 minutes an additional charge of £1 will be added
- The Vehicle Type argument should be either 1, 2 or 3

## Technical Requirements:
- Data should be stored in memory and does not need to be persisted to disk. 
- Basic error handling should be implemented.
- Some Unit tests should be included - we do not expect everything to be covered by a test for this take home assessment.
- The following are the API endpoints to be defined:

### Tasks
#### 1. `GET /parking`
 
- Description: Gets available and occupied number of spaces
- Query Params: None
- Request Body: None
- Response:
```json
{
  "availableSpaces": int,
  "occupiedSpaces": int
}
```

#### 2. `POST /parking`

- Description: Parks a given vehicle in the first available space and returns the vehicle and its space number
- Query Params: None
- Request Body:
```json
{
  "vehicleReg": string,
  "vehicleType": int
}
```
- Response:
```json
{
  "vehicleReg": string,
  "spaceNumber": int,
  "timeIn": date-time
}
```

#### 3. `POST /parking/bill`

- Description: Frees up this vehicles space and return its final charge from its parking time until now 
- Query Params: None
- Request Body:
```json
{
  "vehicleReg": string,
}
```
- Response:
```json
{
  "billId": string,
  "vehicleReg": string,
  "vehicleCharge": double,
  "timeIn": date-time,
  "timeOut": date-time
}
```

Non Technical Requirements:
---------------------------
You should provide a README with your solution it should:
- Include information about how to setup and run your solution locally
- Detail any assumptions you made
- You could include any questions you would’ve asked had you needed to


## GitHub Copilot and AI use
We understand and appreciate that tools like GitHub Copilot have become valuable resources for many developers, enhancing productivity and efficiency in daily work. However, to ensure a fair and accurate assessment of your skills during this interview process, we kindly ask that you complete the assessment task without using Copilot or similar AI coding assistants. This will help us better understand your problem-solving approach and coding abilities. Thank you for your cooperation and understanding.
