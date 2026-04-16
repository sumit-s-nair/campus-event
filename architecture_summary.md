# Architecture Summary: SOLID & GRASP in Campus Event Platform

This document summarizes how the SOLID and GRASP object-oriented design principles were applied to the `campus-event-platform` codebase.

---

## 1. SOLID Principles

### Single Responsibility Principle (SRP)
**Concept:** A class should have one, and only one, reason to change.
**Application:** Introduced **Data Transfer Objects (DTOs)** such as `EventCreateDTO` and `EventResponseDTO`. 
* The `Event` entity is now strictly responsible for database mapping.
* The DTOs are strictly responsible for structuring API payloads. Changes to the database schema no longer accidentally break the frontend, and vice versa.

### Open/Closed Principle (OCP)
**Concept:** Software entities should be open for extension but closed for modification.
**Application:** Replaced hardcoded status update methods (`submitEvent`, `publishEvent`) with a single, generic `updateEventStatus(Long id, StatusUpdateDTO statusUpdate)` method in the Service and Controller. If a new `EventStatus` (e.g., `CANCELLED`) is added tomorrow, the Service and Controller remain **closed to modification** while the system's behavior is **extended**.

### Liskov Substitution Principle (LSP)
**Concept:** Objects of a superclass shall be replaceable with objects of its subclasses without breaking the application.
**Application:** Avoided creating a deep, brittle inheritance hierarchy (e.g., avoiding `class Student extends User`, `class Faculty extends User`). By utilizing a `Role` Enum within a single `User` entity, the application avoids violating LSP while maintaining flexibility.

### Interface Segregation Principle (ISP) & Dependency Inversion Principle (DIP)
**Concept:** * **ISP:** No client should be forced to depend on methods it does not use.
* **DIP:** Depend upon abstractions, not concretions.
**Application:** Extracted the `EventService` class into an interface and created `EventServiceImpl`. The `EventController` now depends purely on the `EventService` interface (the abstraction). This decouples the web layer from the concrete business logic and allows for easy mocking during unit testing.

---

## 2. GRASP Principles

### Information Expert
**Concept:** Assign responsibility to the class that has the information necessary to fulfill it.
**Application:** Moved the status transition logic out of the `EventService` and into a `transitionTo(EventStatus newStatus)` method inside the `Event` entity itself. Because the `Event` holds its own status data, it is the expert on how and when that status should change.

### Creator
**Concept:** Class B should create instances of Class A if Class B aggregates, contains, or closely uses Class A.
**Application:** Delegated the creation of `Participation` objects to the `Event` entity by adding a `createParticipation(User student)` factory method. The `ParticipationService` no longer manually instantiates and glues these models together.

### Low Coupling & High Cohesion
**Concept:** Minimize dependencies between classes (Low Coupling) and keep highly related responsibilities grouped together (High Cohesion).
**Application:** In `ParticipationController`, we mapped the raw `Participation` entity to a `ParticipationResponseDTO` before returning it. This broke the tight coupling between the database layer (which fetches entire `User` and `Event` objects) and the web layer, preventing sensitive data (like passwords) from leaking to the frontend.

### Pure Fabrication
**Concept:** Create artificial classes (not reflecting a real-world domain concept) to achieve low coupling and high cohesion.
**Application:** All the DTOs (`EventCreateDTO`, `StatusUpdateDTO`, etc.) are pure fabrications. They don't exist in the "real world" of a campus event, but they are fabricated to safely transport data across the network.

### Polymorphism
**Concept:** When related alternatives or behaviors vary by type, assign the responsibility for the behavior to the types themselves using polymorphic operations.
**Application:** Added an abstract `canAuthorize(EventStatus targetStatus)` method directly to the `Role` Enum. Instead of writing massive `if (user.role == FACULTY)` chains in the service layer, each specific Role type (e.g., `STUDENT`, `ADMIN`) polymorphicly dictates its own permission rules. The `Event` entity simply asks the user's role if it is authorized.
