# TI Import API

`ti-import-worker` is a Spring Boot microservice of the **Training Internal (TI) Knowledge Platform** responsible for importing questions from **CSV and Excel** files.

The service processes long-running imports asynchronously using **RabbitMQ** and stores imported questions through the Knowledge Service.

## Technology Stack

| Category          | Technology                  |
| ----------------- | --------------------------- |
| Language          | Java 21                     |
| Framework         | Spring Boot 4.0.7           |
| Build             | Gradle                      |
| API               | REST / Spring MVC           |
| Persistence       | Spring Data JPA / Hibernate |
| Security          | Spring Security / OAuth 2.0 |
| Identity Provider | Okta                        |
| **Messaging**         | RabbitMQ                    |
| API Documentation | Springdoc OpenAPI           |
| Metrics           | Micrometer / Prometheus     |
| Tracing           | Micrometer Tracing / Brave  |
| Containerization  | Docker                      |
| **File Formats**      | CSV / Excel                 |

## Architecture

The Import API is part of the asynchronous processing flow:

```text
User
 │
 ▼
Gateway
 │
 ▼
Orchestrator Service
 │
 │ ImportRequestedEvent
 ▼
RabbitMQ
 │
 ▼
Import Service
 │
 │ Validate & Parse
 ▼
Knowledge Service
 │
 ▼
Knowledge Database
 │
 │ ImportCompletedEvent
 ├──────────────► Orchestrator → SSE → UI
 │
 └──────────────► Audit Service
```

RabbitMQ is used for long-running operations such as:

* Import
* Export
* Notifications
* Audit logging

## File Processing

The service uses a parser abstraction for supported file formats:

```text
DataParser
 ├── CSVDataParser
 └── ExcelDataParser
```

CSV files are processed using **Apache Commons CSV**, while Excel files are processed using **FastExcel Reader**.

The imported data is converted into `QuestionRow` objects and then persisted as questions.

## Prerequisites

* Java 21
* Docker
* PostgreSQL
* RabbitMQ
* Okta OAuth 2.0 configuration

The project uses the Gradle Wrapper, so Gradle does not need to be installed separately.

Check Java:

```bash
java -version
```

## Build

Build the application:

```bash
./gradlew clean build
```

Build without tests:

```bash
./gradlew clean build -x test
```

The generated JAR is available in:

```text
build/libs/
```

## Run Locally

Start the application:

```bash
./gradlew bootRun
```

Default port:

```text
8083
```

Application URL:

```text
http://localhost:8083
```

## Configuration

Main configuration:

```text
src/main/resources/application.yaml
```

Local configuration:

```text
src/main/resources/application-local.yaml
```

RabbitMQ, PostgreSQL, and OAuth 2.0/Okta connection settings should be configured through the appropriate application configuration or environment variables.

## Observability

The service provides operational observability through:

* **Micrometer / Prometheus** — application and business metrics
* **Micrometer Tracing ** — distributed tracing
* **Spring Boot Actuator** — health and operational endpoints

These capabilities allow import processing to be monitored across the Gateway, Orchestrator, RabbitMQ, Import Service, Knowledge Service, and Notification/Audit services.
