# Cinema Room REST Service

### Introduction

Welcome to the Cinema Booking System, a simple web application for booking and managing cinema seats. This application is built using the Spring Boot framework and provides RESTful API endpoints for booking, returning, and retrieving information about available seats and statistics.

### API Endpoints

#### 1. Get available seats
* Endpoint: GET "/seats"
* Description: Retrieve information about the available cinema seats.
#### 2. Book a seat
* Endpoint: POST "/purchase"
* Description: Book a cinema seat by providing seat information.
* Example Request Body:
```json
{
  "row": 3,
  "column": 4
}
```
* Example response:
```json
{
    "token": "e739267a-7031-4eed-a49c-65d8ac11f556",
    "ticket": {
        "row": 3,
        "column": 4,
        "price": 10
    }
}
```
#### 3. Return a seat
* Endpoint: POST "/return"
* Description: Return a booked cinema seat by providing a token.
* Example Request Body:
```json
{
    "token": "e739267a-7031-4eed-a49c-65d8ac11f556"
}
```
* Example response:
```json
{
    "returned_ticket": {
        "row": 1,
        "column": 2,
        "price": 10
    }
}
```
#### 4. Get statistics
* Endpoint: GET /stats
* Description: Retrieve statistics about the cinema, including current income, available seats, and purchased tickets.
* Query Parameter: password (required)
* Example Request: 
  * GET /stats?password=super_secret
* Example response:
```json
{
  "current_income": 30,
  "number_of_available_seats": 80,
  "number_of_purchased_tickets": 3
}
```