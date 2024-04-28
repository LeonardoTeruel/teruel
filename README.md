# Teruel - A Payment POC - My Solution for the Playtomic Test
 This project is the resolution made by me (Leonardo Teruel) for the test for Playtomic backend senior software engineer position for their Payments Team.

## Description
 The Playtomic company has a digital wallet for each of the users of their platform. The users can topup this wallet with money using a credit card. They can also see their balance and spend the money buying products, services, renting equipments, booking events, etc, through the platform. 
 This service is a POC of this Payment Service that controls the digital wallet. 

## Features
- **Wallet Top-Up**: Users can top up their wallet balances using credit cards.
- **Wallet Balance Inquiry**: Users can check their wallet balances.

## Technologies Used
- Java 21
- Gradle
- Spring Boot 3
- Spring JPA
- PostgreSQL
- Lombok
- SLF4J for logging
- JUnit and Mockito for testing

## Project Structure
 The project follows Clean Architecture principles, organized into the following packages:

- `application`: Contains the use cases and business logic.
- `domain`: Contains the domain model, entities and repositories contracts related to it.
- `infrastructure`: Contains implementations of repositories, external service clients, logging (if needed).
- `presentation`: Contains the controllers for handling HTTP requests and responses and also global handler exceptions.

## Endpoints  
  The project contains 2 endpoints:

 - `GET /wallet/{{userId}}/balance`:
  - Get a wallet balance using its identifier (In this case I'm using the userId that is also the identifier of the user, because I'm assuming one user can have only one wallet forever and with this assumption I have created the wallet with a composed primary key that is walletId + userId).

- `POST /payments/topup`:
 - Top-up money in the wallet using a credit card number. It charges the amount internally using a third-party platform.

  A collection of the endpoints to be acessed through Postman is available on the root of this project.

## Configuration
- Database Configuration: Update `application.properties` with your database credentials (in a production environment it should be on environment variables or cloud config services, vault, or other more secure way).
- Payment Gateway Configuration: The simulator of the payment gateway was given by Playtomic and is located in `application.properties`.

## Setup Instructions (Locally)

1. Clone the repository: `git clone <https://github.com/LeonardoTeruel/teruel>`
2. Navigate to the project directory
3. Build the project: `./gradlew build`
4. Run the application: `./gradlew bootRun`

## Manual Testing
 You can test it manually calling the endpoints directly using a tool like Postman, but before doing it, you may need to have some registrations on the database, here are examples of the 3 inserts you can use to prepare the database: 
 
 **Insert an user**
 `INSERT INTO users (id, email, password, username)
 VALUES ('c2d29867-3d0b-d497-9191-18a9d8ee7830', 'any@gmail.com','anypwd','any');`
 
 **Create the wallet for the user**
 `INSERT INTO wallet (user_id, wallet_id,balance, created_at)
 VALUES ('c2d29867-3d0b-d497-9191-18a9d8ee7830', 'c3d29867-3d0b-d497-9191-18a9d8ee7837',0, '2024-04-27');`
 
 **Insert the TOP_UP transaction type**
 `INSERT INTO transaction_type (id,name)
 VALUES (1,'TOP_UP');`

## Automatic Units Testing
Run unit tests: `./gradlew test`

  
  

 
