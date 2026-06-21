# Clean Architecture: Customer Service

A demo Spring Boot service built to illustrate **Clean Architecture** (Uncle Bob), keeping the
application **core** completely isolated from frameworks and the outside world.

> The core idea: the access flow always goes **from the outside in**. Frameworks, databases and
> HTTP clients depend on the business rules. Never the other way around.

Reference (PT-BR): [Descomplicando a Clean Architecture: Luizalabs](https://medium.com/luizalabs/descomplicando-a-clean-architecture-cf4dfc4a1ac6)

## The layers

![Clean Architecture](docs/Clean%20Architecture/image.png)

| Ring | Meaning                                                           |
|------|-------------------------------------------------------------------|
| **Entities** | Domain classes: the business objects of the application           |
| **Use Cases** | Application-specific business rules (flow control, orchestration) |
| **Interface Adapters** (green) | Communication layer between the inner and the outer world         |
| **Frameworks & Drivers** (blue) | The outside world: DB, HTTP, messaging                            |

Compared to the traditional Spring layered approach:

![Layered vs Clean](docs/Clean%20Architecture/image%202.png)

| Layered (Spring)            | Clean Architecture           |
|-----------------------------|------------------------------|
| Entity / model              | `core.domain`                |
| Service                     | `core.usecase`               |
| Controller / DTO            | `entrypoint`                 |
| Repository / external client| `dataprovider`               |

## Package structure

![Simple Clean Architecture](docs/Clean%20Architecture/image%201.png)

The package layout enforces the **dependency rule**: `core` must not depend on `dataprovider`,
`entrypoint`, or any framework. The outer layers depend on `core`: never the reverse.

```
com.paschoalick.cleanarch
├── core                      # framework-free business layer
│   ├── domain                # plain Java POJOs (Customer, Address): no Lombok, no Spring
│   ├── dataprovider          # output ports: interfaces the use cases depend on
│   │   ├── InsertCustomer
│   │   └── FindAddressByZipCode
│   └── usecase               # use case interfaces + impl (constructor injection only)
│       └── impl
│
├── dataprovider              # infrastructure adapters (Spring lives here)
│   ├── InsertCustomerImpl            # @Component implementing core ports
│   ├── FindAddressByZipCodeImp
│   ├── client                # OpenFeign client + response DTO + MapStruct mapper
│   └── repository            # Spring Data MongoDB repo + entity documents + mapper
│
└── entrypoint               # how the application is accessed
    └── controller            # REST controller + request DTO + mapper
```

> **POJO**: *Plain Old Java Object*. A regular Java class with no ties to any framework,
> specification, or special interface. The `core.domain` classes are intentionally hand-written
> POJOs so the domain stays library-free.

### Conventions to preserve when extending

- New domain types and use cases go under `core` and must stay free of Spring / Lombok / MapStruct imports.
- Lombok (`@Data`) and Spring annotations belong only to `dataprovider` adapters, `entrypoint`, entities, and DTOs.
- Each external concern (DB, HTTP) is reached through a `core.dataprovider` interface (the *port*),
  implemented by a `@Component` *adapter* in `dataprovider`.
- Domain types and persistence entities are kept separate and mapped at the `dataprovider` boundary.

## Request flow

Inserting a customer (`POST /api/v1/customers`):

```
CustomerController          (entrypoint)
  → InsertCustomerUseCase   (core: orchestrates the rule)
      → FindAddressByZipCode  (core port) → Feign client → external address service
      → InsertCustomer        (core port) → MongoDB repository
```

`InsertCustomerUseCaseImpl` resolves the address from the zip code, attaches it to the customer,
and persists it: speaking only in domain types, with no framework imports.

## Tech stack

- **Java 21**, **Spring Boot 4.1.0**, **Spring Cloud 2025.0.0**
- Spring Web, Spring Data MongoDB, Spring Validation
- Spring Cloud OpenFeign (external address lookup)
- Spring for Apache Kafka (consumer entrypoint: planned)
- MapStruct (DTO/entity ↔ domain mapping) and Lombok (outer layers only)
- Gradle wrapper

## Runtime dependencies

`application.yml` expects:

- **MongoDB** at `mongodb://root:example@localhost:27017/cleanarch` (auth source `admin`)
- The **address-lookup REST service** at `paschoalick.client.address.url`
  (default `http://localhost:8082/address`), consumed via OpenFeign

## Build & run

The build tool is the Gradle wrapper (`./gradlew`).

```bash
# Build
./gradlew build

# Run the application
./gradlew bootRun

# Run all tests
./gradlew test

# Run a single test class
./gradlew test --tests 'com.paschoalick.cleanarch.CleanarchApplicationTests'

# Run a single test method
./gradlew test --tests 'com.paschoalick.cleanarch.CleanarchApplicationTests.contextLoads'
```

> `./gradlew test` (and `build`) boots a Spring context (`@SpringBootTest`), so an external
> MongoDB and the address Feign endpoint configured in `application.yml` may be required for some
> tests to pass.

## API

### Create a customer

```http
POST /api/v1/customers
Content-Type: application/json
```

```json
{
  "name": "John Doe",
  "cpf": "12345678900",
  "zipCode": "01001000"
}
```

All fields are `@NotBlank`. The service resolves the full address from `zipCode` via the external
address service before persisting. Returns `200 OK` with an empty body on success.

## Status

Work in progress. The Kafka consumer entrypoint is planned but not yet implemented.
