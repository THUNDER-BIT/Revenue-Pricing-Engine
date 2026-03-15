Revenue Pricing Engine

A precision-focused financial tool built to calculate Total Contract Value (TCV) for complex, multi-year SaaS "ramp" deals. This engine allows users to define multiple pricing segments and calculates the total revenue impact using a high-precision math engine.
Backend: Java 18, Spring Boot 3.5

Tech Stack
Backend: Java 17, Spring Boot 3.5.x

Frontend: Thymeleaf, Bootstrap 5, JavaScript (Fetch API)

Build Tool: Maven

Testing: JUnit 5

Data Format: JSON

Architecture & Data Flow
The project follows a decoupled 3-Tier Architecture to ensure scalability and separation of concerns:

Client Layer (UI): A responsive Bootstrap 5 dashboard. It uses a dynamic HTML table where users can add/remove rows. JavaScript's fetch() API captures this data and sends it as a JSON payload to the server.

Controller Layer (API Gateway):

BillingUIController: Serves the Thymeleaf-based web views.

BillingRestController: The API entry point that handles JSON deserialization and coordinates with the service layer.

Service Layer (The Brain): Contains the core business logic. It uses the Java Streams API to process segments with high efficiency and handles financial data using BigDecimal to avoid floating-point errors.

Model Layer (Domain): Defined RampSegment as a POJO (Plain Old Java Object) to represent the data contract between frontend and backend.