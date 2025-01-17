# Hands On With - Java Async Programming

> Learning Asynchronous Programming in Java

## Concepts Covered

- Blocking v/s Non-blocking Threads, Thread Pools
- Threads, Synchronization, Thread Methods, Cyclic Barrier, Countdown Latch
- Runnable, Callable, Future, CompletableFuture, Parallel Stream
- Inter-thread Communication, Result Processing, Exception Handling
- Race, Dead Lock, Semaphores

## Premise of Examples

### Data Models

### Use Cases

#### Transactions Use Cases

- **T01:** Get all transactions for a given date range
- **T02:** Get all successful transactions in a given date range
- **T03:** Get all failed transactions in a given date range
- **T04:** Get sum, max, min & average of successful transactions in given date range
- **T05:** Get sum, max, min & average of failed transactions in given date range

#### Merchants Use Cases

- **M01:** Get all transactions for given merchant for given date range
- **M02:** Get all successful transactions for given merchant for given date range
- **M03:** Get all failed transactions for given merchant for given date range
- **M04:** Get sum, max, min & average of successful transactions for given merchant for given date range
- **M05:** Get sum, max, min & average of failed transactions for given merchant for given date range

#### Stores User Cases

- **S01:** Get all transactions for given store for given date range
- **S02:** Get all successful transactions for given store for given date range
- **S03:** Get all failed transactions for given store for given date range
- **S04:** Get sum, max, min & average of successful transactions for given store for given date range
- **S05:** Get sum, max, min & average of failed transactions for given store for given date range

#### Special Use Cases

Idea here is to use multiple threads to gather data of different range or 
filters then combine them together to prepare final result

- **Reconciliation01:** Get all transactions for a year
- **Reconciliation02:** Get sum of successful transactions per month for a given year
- **Reconciliation03:** Get month & count of highest failed transactions per month for a given year
- **Reconciliation04:** Compare sales per merchant for a given year with previous year
- **Reconciliation05:** Compare sales for a given year between two merchants

## API

### Server API

### Client API