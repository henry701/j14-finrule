# j14-finrule

Small API developed in Java 14 with Spring for scheduling financial transfers.

## Operations

- Transfer Scheduling:
    - Calculates fee and feeType based on business rules.
- Transfer Cancellation:
    - Cancels a transfer, provided it has not already been carried out.
- Transfer Query:
    - Query transfers by their numeric ID to read data about them.
- Transfer List:
    - Lists all past and future transfers.

## How to Use

There is an [OpenAPI 3.0](https://github.com/OAI/OpenAPI-Specification/blob/master/versions/3.0.0.md) file [in this repository](./openapi.yaml)
that may be read by [documentation generation tools](https://openapi.tools/#documentation). It's the `openapi.yaml` file at the root of this repository.

There is also a [Postman](https://www.getpostman.com) collection in the repository root: `postman_collection.json`.

## Languages, Frameworks and Tools

- Java 14
    - A requirement of the application: "Use the latest Java version".
- Spring Boot
    - The go-to framework for implementing modern REST micro-services in Java.
- H2 Database
    - A database with in-memory capabilities, to satisfy the requirement "Data persistence in memory only" while still being able to use JPA.
- Docker
    - For easy setup of the application without the need to install a Java 14 JDK yourself.
- Docker Compose
    - Used to configure some parameters of the application's Docker image without long command lines.
- JUnit 4
    - The go-to test framework for unit tests in Java, with Spring integration available.
- Logback
    - Default logging framework for Spring, also widely used for other kinds of Java applications.
- Lombok
    - To avoid writing boilerplate code (like getters and setters, toString, equals) in application models.
    
## How to Add New Rules

Simply create a class that implements the `FinancialFeeRule` interface, annotate it with `@Service` and define its precedence in the rule chain by using `@Order`.

Example class header for a class with `-1` order, which is processed before any class with an order value than it (because it will be resolved first due to lower order):
```java
@Service
@Order(value = -1)
public class MyCustomRule implements FinancialFeeRule
{
    // ...
}
```

## Runtime Requirements

### With Docker
- Docker and Docker Compose installed
    - Docker Desktop (for Windows users): v2.3.3.2 (46784)
    - Docker Engine: v19.03.12
    - Docker Compose: 1.26.2

### Without Docker
- Java 14 runtime installed

## Development Requirements

To build the project, you will need at least:
- Maven 3.0
- JDK 14

## Configurations

In the `application.properties` file located at the root directory of the ZIP archive
and inside the resources folder (`src/main/resources`) of this project,
you may alter some parameters, such as the port the API is exposed on.

## Deploy Instructions

### For Development using Docker

Build:
```bash
mvn clean install
```

Run:
```bash
mvn exec exec:docker-up
```

Stop:
```bash
mvn exec exec:docker-stop
```

### Deploying in Production

After the build, a ZIP archive is generated with the artifacts and dependencies of the application in the target folder,
following the name template `j14-finrule-`**`VERSION`**`.zip`.

Extract this ZIP archive in a folder on the target machine.

#### Deploying with Docker

For deploying in production, run:
```bash
docker-compose up --build
```
In the same working directory where the archive was extracted.

#### Deploying without Docker

In case the production machine does not have Docker installed,
you may also run the application as a simple Java process, executing:
```bash
java -Dlogging.config=app/logback-spring.xml -cp "app:app/lib/*" "br.com.henry.finrule.Application"
```
In the same working directory where the archive was extracted.
