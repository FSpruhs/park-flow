# Realistic Small

A realistic scenario for a medium parking lot. The scenario has two entrances. There are six waves of vehicles entering the parking lot through the entrance.
Each wave has 200 vehicles split on both entrances, and between each wave is a pause of 20 minutes.

## Overview

**Number of Parking Spots**: 1.000 \
**Number of Gates**: 2 \
**Number of Vehicles**: 1.200 \
**Time to reach the entrance**: 8–12 seconds \
**Time to reach the parking spot**: 1–3 minutes \
**Parking time**: 20–30 minutes \
**Time to reach the exit**: 1–3 minutes

## Successful Run

| Used Heap          | Max CPU | Runnable Thread Count | DB Events | DB Snapshots | Events Consumed Total  | Events Consumed Rate | Events Published Total | Events Published Rate | Rabbitmq Received Total | Rabbitmq Received |
|--------------------|---------|-----------------------|-----------|--------------|------------------------|----------------------|------------------------|-----------------------|-------------------------|-------------------|
| 473 MiB / 16,6 GiB | 17.1%   | 58                    | 6.704 kb  | 272 kb       | 29.213                 | 1/sek                | 10.604                 | 1/sek                 | 7.200                   | 1,5/sek           |

![Successful Run](solution-logs.png)

### System Metrics

### Pictures

#### JVM – Memory
![JVM - Memory](jvm-memory.png)

#### JVM - Memory Pools
![JVM - Memory Pools](jvm-memory-pools.png)

#### JVM - Misc
![JVM - Misc](jvm-misc.png)

#### Garbage Collection
![Garbage Collection](garbage-collection.png)

#### DB - Events
![DB - Events](db-event-speicher.png)

#### DB – Snapshots
![DB - Snapshots](db-snapshot-speicher.png)

#### Spring Events Consumed
![Spring Event Consumed](spring-events-consumed.png)

#### Spring Events Published
![Spring Event Published](spring-events-published.png)

#### RabbitMQ – Received
![RabbitMQ](rabbitmq-received.png)

#### Vehicles Parked Correct
![Vehicles Parked Correct](vehicles-parked-correct.png)
