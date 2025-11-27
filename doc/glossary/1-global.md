# Global Terms

These domain terms are used across multiple bounded contexts.

| Term              | Definition                                                                                                     |
|-------------------|----------------------------------------------------------------------------------------------------------------|
| Vehicle           | A vehicle identified primarily by its license plate number.                                                    |
| Customer          | A person or entity using the parking inventory.                                                                |
| Parking Stuff     | Employees or operators responsible for managing and overseeing the parking facility and its operations.        |
| Parking Lot       | The entire parking facility or area, including all parking spots, entrances/exits, and related infrastructure. |
| Parking Inventory | Represents the complete set of parking resources in the facility, including all parking spots and gates.       |
| Plate Number      | The unique license plate identifier of a vehicle, used for verification and access control.                    |

These technical terms are used across multiple bounded contexts.

| Term                   | Definition                                                                                                     |
|------------------------|----------------------------------------------------------------------------------------------------------------|
| Aggregate              | A DDD building block representing a cluster of domain objects handled as a single unit.                        |
| Aggregate Root         | The primary entity controlling access to the aggregate’s internal state. All changes go through it.            |
| Event Sourcing         | A persistence approach where state is stored as a sequence of immutable events.                                |
| Domain Event           | An immutable message describing something that happened in the domain.                                         |
| Event Serializer       | Component that transforms domain events to byte arrays and back for storage.                                   |
| Snapshot               | A stored state representation of an aggregate used to speed up loading by avoiding reapplying all past events. |
| Event Metadata         | Additional information stored alongside events, e.g., imported flag or timestamp.                              |
| Event Importing        | Process of copying events from one aggregate into another while marking them as imported.                      |
| Port                   | A hexagonal architecture concept defining the boundary between domain logic and external systems.              |
| Adapter                | Implementation of a port for a specific technology (REST, DB, Message Queue, etc.).                            |
| Hexagonal Architecture | Architectural style that decouples core logic from external infrastructure via ports & adapters.               |
| Modulith               | A modular monolith: one deployable unit with clear internal module boundaries.                                 |
| Command                | Request that triggers a state change in an aggregate.                                                          |
| Event Publisher        | Component responsible for publishing domain events after persistence.                                          |
| Imported Event         | An event applied for state synchronization but not republished.                                                |
|                        |                                                                                                                |
|                        |                                                                                                                |
|                        |                                                                                                                |
