# Payment Gateway Integration Project

## Introduction

Welcome to the Payment Gateway Integration Project. This project demonstrates the integration of a Core Payments System (CPS) with a 3rd Party Payment Gateway (PG) using the Daraja B2C API. The integration involves handling payment requests, managing transaction statuses, and interfacing with the Core Payments System.

## Scope

This project encompasses:

- Event-driven and non-blocking programming
- Java development using Spring Boot
- RESTful API interactions
- SQL and NoSQL databases
- OAuth 2.0 authentication
- Task scheduling and retry mechanisms
- Source code documentation and testing

## Terminology

- **PG**: Payment Gateway
- **CPS**: Core Payments System
- **API**: Application Programming Interface
- **GwRequest**: JSON object containing payment request details
- **Result**: JSON object containing payment response details

## Task Overview

### 1. Receive Payment Request from CPS

1. **Receive**: Listen to Kafka topic for incoming `GwRequest`.
2. **Validate**: Check for mandatory fields and data types.
3. **Log**: Store request details in the database with a status of "Pending".
4. **Send**: Forward validated request to 3rd Party PG.
5. **Handle Failures**: Log failed requests for retry.

### 2. Validate, Log & Send to 3rd Party PG

1. **Validate**: Ensure `mobileNumber` and `amount` are correct.
2. **Log**: Save request with status "Pending".
3. **Send Request**: Package and send request to PG.
4. **Handle Response**: Log and update status, retry if necessary.

### 3. Receive 3rd Party Callback

1. **Receive Callback**: Handle PG's response.
2. **Validate**: Check payload and request existence.
3. **Update Log**: Adjust status and remove from pending logs.
4. **Send Result**: Package result and forward to CPS via Kafka.

### 4. Query Payment Status

1. **Recurring Task**: Periodically check pending logs.
2. **Update Status**: Reflect current status in logs.
3. **Handle Results**: Send updated results to CPS and clean up logs.

## System Architecture

The system architecture includes:

- **Core Payments System (CPS)**
- **Payment Gateway (PG)**
- **Kafka for messaging**
- **Database (MariaDB or MongoDB)**
- **Spring Boot for application development**

## Tech Stack

### Option 1: Spring, MariaDB & Kafka

- **Backend**: Spring Boot, Spring WebMVC, Spring Data JPA
- **Database**: MariaDB
- **Message Broker**: Apache Kafka
- **Containerization**: Docker

## Setup and Installation

### Prerequisites

- Java 11 or higher
- Docker
- Apache Kafka and Zookeeper
- MariaDB or MongoDB (depending on your choice)

### Build the Project

```bash
./mvnw clean install
###Run the Application
bash
./mvnw spring-boot:run
Start Kafka and Zookeeper
Use Docker Compose for convenience:

bash
docker-compose up
Configure the Application
Update application.properties with your database and Kafka settings.

Run Tests
The code includes tests for all key methods to ensure functionality and reliability:

bash
./mvnw test