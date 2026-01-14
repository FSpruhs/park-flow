# Realistic Small Scenario

A realistic scenario for a small parking lot. The scenario has one entrance. There are three waves of vehicles entering the parking lot through the entrance.
Each wave has 50 vehicles. 

## Overview

**Number of Parking Spots**: 100 \
**Number of Gates**: 1 \
**Number of Vehicles**: 150 \
**Time to reach the entrance**: 8–12 seconds \
**Time to reach the parking spot**: 1–2 minutes \
**Parking time**: 10–20 minutes \
**Time to reach the exit**: 1–2 minutes

## Successful Run

| Category         | Value           | small            |
|------------------|-----------------|------------------|
| JVM Memory       |                 |                  |
|                  | max. used       | 505 MiB          |
|                  | median used     | 361 MiB          |
|                  | mean used       | 367 MiB          |
|                  | max. commited   | 591 MiB          |
|                  | median commited | 590 MiB          |
|                  | mean commited   | 558 MiB          |
|                  | max. maximal    | 16,6 GiB         |
|                  | median maximal  | 16,6 GiB         |
|                  | mean maximal    | 16,6 GiB         |
| CPU Usage        |                 |                  |
|                  | max. system     | 3,5%             |
|                  | median system   | 0,8%             |
|                  | mean system     | 1,0%             |
|                  | max. process    | 1,6%             |
|                  | median process  | 0,0%             |
|                  | mean process    | 0,1%             |
| Threads          |                 |                  |
|                  | max. live       | 117              |
|                  | max. peak       | 117              |
|                  | max. deamon     | 74               |
|                  | max. runable    | 58               |
| Database Storage |                 |                  |
|                  | Events          | 1.000 kB         |
|                  | Snapshots       | 200 kB           |
| Events Published |                 |                  |
|                  | Total           | 1.302            |
|                  | Max             | 0,8 events/sec   |
|                  | Median          | 0,333 events/sec |
|                  | Mean            | 0,349 events/sec |
| Events Consumed  |                 |                  |
|                  | Total           | 3.557            |
|                  | Max             | 1,89 events/sec  |
|                  | Median          | 0,8 events/sec   |
|                  | Mean            | 0,851 events/sec |
| Events RabbitMQ  |                 |                  |
|                  | Total           | 900              |
|                  | Max             | 0,689 events/sec |
|                  | Median          | 0,289 events/sec |
|                  | Mean            | 0,301 events/sec |


![Successful Run](solution-logs.png)

### System Metrics

### Pictures

#### JVM – Memory
![JVM - Memory](jvm-misc.png)

#### JVM - Memory Pools
![JVM - Memory Pools](jvm-memory-pools.png)

#### JVM - Misc
![JVM - Misc](jvm-memory.png)

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
