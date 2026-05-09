# Webhook Delivery System

A production-grade webhook delivery system built with Spring Boot — similar to how Razorpay and Stripe notify merchants about payment events.

## What it does

When something happens in your application (like a payment success), this system automatically notifies all registered clients by sending a POST request to their URL. If delivery fails, it retries automatically with exponential backoff.

## Real World Analogy

- You pay on Swiggy via Razorpay
- Razorpay needs to tell Swiggy "payment done, start the order"
- Instead of Swiggy checking every second, Razorpay POSTs to Swiggy's URL instantly
- That's exactly what this system does

## Architecture
Event Triggered
↓
Published to Kafka
↓
Worker picks it up
↓
Signs payload with HMAC-SHA256
↓
POSTs to client URL
↓
If fails → Retry with exponential backoff
↓
Every attempt logged in PostgreSQL

## Features

- **Client Registration** — clients register their URL and which events they want
- **Async Event Publishing** — events published to Kafka for reliable async processing
- **HMAC-SHA256 Signing** — every request is signed so clients can verify authenticity
- **Exponential Backoff Retry** — retries on failure after 2, 4, 8, 16 minutes
- **Delivery Logs** — every delivery attempt stored in PostgreSQL with status and latency
- **Dockerized** — runs with a single docker-compose command

## Tech Stack

- Java 17
- Spring Boot 3.2.5
- Apache Kafka
- PostgreSQL
- Docker + Docker Compose
- HMAC-SHA256

## Project Structure
src/main/java/com/example/webhook/
├── client/          ← registration API
├── event/           ← event publishing
├── delivery/        ← worker, dispatcher, retry
└── log/             ← delivery logs

## Getting Started

### Prerequisites

- Java 17
- Docker Desktop
- Maven

### Run the project

**Step 1 — Start infrastructure:**
```bash
docker-compose up -d
```

**Step 2 — Start the app:**
```bash
mvn spring-boot:run
```

App runs on `http://localhost:8080`

## API Reference

### Register a client
POST /api/clients
Content-Type: application/json
{
"name": "Swiggy",
"endpointUrl": "https://your-url.com/webhook",
"eventType": "PAYMENT_SUCCESS"
}

Response:
```json
{
  "id": "uuid",
  "name": "Swiggy",
  "endpointUrl": "https://your-url.com/webhook",
  "secret": "auto-generated-secret",
  "active": true,
  "subscribedEvent": "PAYMENT_SUCCESS"
}
```

### Trigger an event
POST /api/events/trigger
Content-Type: application/json
{
"type": "PAYMENT_SUCCESS",
"orderId": "ORD-123",
"amount": "500"
}

### View delivery logs
GET /api/logs/{clientId}

## How HMAC Signing Works

Every webhook request includes a header:
X-Webhook-Signature: sha256=<hash>

The client can verify this by hashing the payload with their secret key. If it matches, the request is genuine.

## How Retry Works

| Attempt | Wait time |
|---------|-----------|
| 1st retry | 2 minutes |
| 2nd retry | 4 minutes |
| 3rd retry | 8 minutes |
| 4th retry | 16 minutes |
| 5th retry | Mark as DEAD |

## Supported Event Types

- `PAYMENT_SUCCESS`
- `ORDER_PLACED`
- `ORDER_CANCELLED`

## Resume Description

> Designed and implemented a webhook delivery system with Kafka-based async event publishing, HMAC-SHA256 payload signing, exponential backoff retry (5 attempts), and delivery audit logging — deployed via Docker Compose.
