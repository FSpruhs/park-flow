# Realistic Large Scenario

A realistic scenario for a large parking lot. The scenario has five entrances. There are four waves of vehicles entering the parking lot through the entrance.
Each wave has 3.000 vehicles split on all entrances.

## Overview

**Number of Parking Spots**: 10.000 \
**Number of Gates**: 5 \
**Number of Vehicles**: 12.000 \
**Time to reach the entrance**: 8–12 seconds \
**Time to reach the parking spot**: 1–4 minutes \
**Parking time**: 20–30 minutes \
**Time to reach the exit**: 1–4 minutes

## Successful Run

| Category         | Value           | medium          |
|------------------|-----------------|-----------------|
| JVM Memory       |                 |                 |
|                  | max. used       | 485 MiB         |
|                  | median used     | 359 MiB         |
|                  | mean used       | 359 MiB         |
|                  | max. commited   | 565 MiB         |
|                  | median commited | 565 MiB         |
|                  | mean commited   | 565 MiB         |
|                  | max. maximal    | 16,6 GiB        |
|                  | median maximal  | 16,6 GiB        |
|                  | mean maximal    | 16,6 GiB        |
| CPU Usage        |                 |                 |
|                  | max. system     | 2,0%            |
|                  | median system   | 1,1%            |
|                  | mean system     | 1,1%            |
|                  | max. process    | 3,2%            |
|                  | median process  | 0,1%            |
|                  | mean process    | 0,3%            |
| Threads          |                 |                 |
|                  | max. live       | 134             |
|                  | max. peak       | 135             |
|                  | max. deamon     | 87              |
|                  | max. runable    | 64              |
| Database Storage |                 |                 |
|                  | Events          | 64 MB           |
|                  | Snapshots       | 1.416 kB        |
| Events Published |                 |                 |
|                  | Total           | 106.010         |
|                  | Max             | 7,76 events/sec |
|                  | Median          | 3,44 events/sec |
|                  | Mean            | 3,82 events/sec |
| Events Consumed  |                 |                 |
|                  | Total           | 292.031         |
|                  | Max             | 19,2 events/sec |
|                  | Median          | 8,44 events/sec |
|                  | Mean            | 9,28 events/sec |
| Events RabbitMQ  |                 |                 |
|                  | Total           | 72.000          |
|                  | Max             | 6,76 events/sec |
|                  | Median          | 3 events/sec    |
|                  | Mean            | 3,28 events/sec |

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
