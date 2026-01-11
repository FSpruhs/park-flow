# Simple Parking Inventory

## Overview

| Parking Spots | Gates | Vehicles | Customers | Events | Duration |
|---------------|-------|----------|-----------|--------|----------|
| 5             | 3     | 0        | 0         | 39     | 1 min    |

## Tested Use Cases

The following use cases are tested:

| #  | Use Case              | Infos                    | Result    |
|----|-----------------------|--------------------------|-----------|
| 1  | CreateParkingSpot     |                          | Success   |
| 2  | CreateParkingSpot     | Type(DISABLED)           | Success   |
| 3  | CreateParkingSpot     | Type(RENTABLE, ELECTRIC) | Success   |
| 4  | CreateParkingSpot     |                          | Success   |
| 5  | CreateParkingSpot     |                          | Success   |
| 6  | CreateGate            | ENTRANCE                 | Success   |
| 7  | CreateGate            | EXIT                     | Success   |
| 8  | CreateGate            | EXIT                     | Success   |
| 9  | CreateParkingSpot     | with existing name       | Error 400 |
| 10 | CreateGate            | with existing name       | Error 400 |
| 11 | DeactivateGate        |                          | Success   |
| 12 | DeactivateGate        |                          | Success   |
| 13 | RemoveGate            |                          | Success   |
| 14 | ActivateGate          |                          | Success   |
| 15 | ActivateGate          | GateId not exists        | Error 404 |
| 16 | ActivateGate          | removed Gate             | Error 400 |
| 17 | RenameParkingSpot     |                          | Success   |
| 18 | AddParkingSpotType    | RENTABLE, ELECTRIC       | Success   |
| 19 | RemoveParkingSpotType | ELECTRIC                 | Success   |
| 20 | AddParkingSpotType    | ELECTRIC                 | Success   |
| 21 | AddParkingSpotType    | RENTABLE to DISABLED     | Error 400 |
| 21 | AddParkingSpotType    | ParkingSpotId not exists | Error 404 |
| 22 | AddParkingSpotType    | RENTABLE without a price | Error 400 |
| 24 | DeactivateParkingSpot |                          | Success   |
| 25 | DeactivateParkingSpot |                          | Success   |
| 26 | ActivateParkingSpot   |                          | Success   |
| 27 | RemoveParkingSpot     |                          | Success   |
| 28 | DeactivateParkingSpot | removed ParkingSpot      | Error 400 |
