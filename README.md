# Clean Architecture: Customer Service

A demo Spring Boot service built to illustrate **Clean Architecture** (Uncle Bob), keeping the
application **core** completely isolated from frameworks and the outside world.

> The core idea: the access flow always goes **from the outside in**. Frameworks, databases and
> HTTP clients depend on the business rules. Never the other way around.

Reference (PT-BR): [Descomplicando a Clean Architecture: Luizalabs](https://medium.com/luizalabs/descomplicando-a-clean-architecture-cf4dfc4a1ac6)

📚 **Documentation**
- [Clean Architecture (conceptual notes, PT-BR)](docs/clean-architecture.md)
- [Find Customer by ID (work-in-progress feature, PT-BR)](docs/find-customer-by-id.md)

## The layers

![Clean Architecture](docs/clean-architecture/image.png)

| Ring | Meaning                                                           |
|------|-------------------------------------------------------------------|
| **Entities** | Domain classes: the business objects of the application           |
| **Use Cases** | Application-specific business rules (flow control, orchestration) |
| **Interface Adapters** (green) | Communication layer between the inner and the outer world         |
| **Frameworks & Drivers** (blue) | The outside world: DB, HTTP, messaging                            |

Compared to the traditional Spring layered approach:

![Layered vs Clean](docs/clean-architecture/image-2.png)

| Layered (Spring)            | Clean Architecture           |
|-----------------------------|------------------------------|
| Entity / model              | `core.domain`                |
| Service                     | `core.usecase`               |
| Controller / DTO            | `entrypoint`                 |
| Repository / external client| `dataprovider`               |

## Package structure

![Simple Clean Architecture](docs/clean-architecture/image-1.png)

The package layout enforces the **dependency rule**: `core` must not depend on `dataprovider`,
`entrypoint`, or any framework. The outer layers depend on `core`: never the reverse.

```
com.paschoalick.cleanarch
├── core                      # framework-free business layer
│   ├── domain                # plain Java POJOs (Customer, Address): no Lombok, no Spring
│   ├── dataprovider          # output ports: interfaces the use cases depend on
│   │   ├── InsertCustomer
│   │   ├── FindAddressByZipCode
│   │   └── SendCpfForValidation   # publishes the CPF for async validation
│   └── usecase               # use case interfaces + impl (constructor injection only)
│       └── impl
│
├── dataprovider              # infrastructure adapters (Spring lives here)
│   ├── InsertCustomerImpl            # @Component implementing core ports
│   ├── FindAddressByZipCodeImp
│   ├── client                # OpenFeign client + response DTO + MapStruct mapper
│   └── repository            # Spring Data MongoDB repo + entity documents + mapper
│
├── config                    # @Configuration / @Bean wiring (use cases + Kafka)
│   ├── InsertCustomerConfig          # builds non-Spring use case impls
│   ├── FindCustomerByIdConfig
│   ├── UpdateCustomerConfig
│   ├── DeleteCustomerByIdConfig
│   ├── KafkaProducerConfig           # ProducerFactory / KafkaTemplate
│   └── KafkaConsumerConfig           # ConsumerFactory / listener container
│
└── entrypoint               # how the application is accessed
    ├── controller            # REST controller + request/response DTO + mapper
    └── consumer              # Kafka consumer + message DTO (CustomerMessage)
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
      → SendCpfForValidation  (core port) → Kafka producer → validation topic
```

`InsertCustomerUseCaseImpl` resolves the address from the zip code, attaches it to the customer,
persists it, and finally publishes the CPF for asynchronous validation: speaking only in domain
types, with no framework imports.

## Tech stack

- **Java 21**, **Spring Boot 3.4.6**, **Spring Cloud 2024.0.1**
- Spring Web, Spring Data MongoDB, Spring Validation
- Spring Cloud OpenFeign (external address lookup)
- Spring for Apache Kafka (CPF validation producer + consumer)
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

## CPF validation flow (Kafka)

Once a customer is created, the CPF is published to a Kafka topic. A (mocked) external API reads
the CPF, validates the information and publishes the result back to another topic; the consumer
on our side picks it up and updates the customer (`isValidCpf`).

![Kafka producer/consumer](docs/kafka/kafka-producer-consumer.png)

## Status

Work in progress. Current state:

- ✅ Customer CRUD use cases (insert, find by id, update, delete) wired through `config` `@Bean` factories.
- ✅ REST entrypoint (`CustomerController`) for the create flow.
- ✅ Kafka producer/consumer configuration (`KafkaProducerConfig` / `KafkaConsumerConfig`) and the
  `SendCpfForValidation` port, plumbed into `InsertCustomerUseCaseImpl`.
- 🚧 `SendCpfForValidationImpl` adapter and the Kafka consumer listener are stubbed and still being
  implemented; the CPF validation round-trip is not yet wired end to end.

A *Find Customer by ID* use case is also being built. See
[docs/find-customer-by-id.md](docs/find-customer-by-id.md) for details.
