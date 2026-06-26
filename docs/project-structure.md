# Project Structure & Class Relationships

Full map of the source tree under `com.paschoalick.cleanarch`, showing how the classes and
interfaces relate across the Clean Architecture layers.

> **Dependency rule:** arrows always point **inward**. `entrypoint`, `config` and `dataprovider`
> may depend on `core`; `core` depends on nothing outside itself (no Spring / Lombok / MapStruct).

## Directory tree

```
com.paschoalick.cleanarch
│
├── CleanarchApplication.java            # @SpringBootApplication entrypoint
│
├── core/                                # framework-free business layer
│   ├── domain/
│   │   ├── Customer.java                # POJO: id, name, cpf, isValidCpf, Address
│   │   └── Address.java                 # POJO: street, city, state, zipCode...
│   │
│   ├── dataprovider/                    # OUTPUT PORTS (interfaces)
│   │   ├── InsertCustomer.java
│   │   ├── FindCustomerById.java
│   │   ├── UpdateCustomer.java
│   │   ├── DeleteCustomerById.java
│   │   ├── FindAddressByZipCode.java
│   │   └── SendCpfForValidation.java
│   │
│   ├── usecase/                         # INPUT PORTS (use case interfaces)
│   │   ├── InsertCustomerUseCase.java
│   │   ├── FindCustomerByIdUseCase.java
│   │   ├── UpdateCustomerUseCase.java
│   │   ├── DeleteCustomerByIdUseCase.java
│   │   └── impl/                        # implementations (constructor injection, no Spring)
│   │       ├── InsertCustomerUseCaseImpl.java
│   │       ├── FindCustomerByIdUseCaseImpl.java
│   │       ├── UpdateCustomerUseCaseImpl.java
│   │       └── DeleteCustomerByIdUseCaseImpl.java
│   │
│   └── SendCpfForValidationImpl.java    # ⚠️ empty stub, misplaced (see Notes)
│
├── config/                             # Spring wiring (@Configuration / @Bean)
│   ├── InsertCustomerConfig.java        # builds InsertCustomerUseCaseImpl bean
│   ├── FindCustomerByIdConfig.java
│   ├── UpdateCustomerConfig.java
│   ├── DeleteCustomerByIdConfig.java
│   ├── KafkaProducerConfig.java         # ProducerFactory + KafkaTemplate
│   └── KafkaConsumerConfig.java         # ConsumerFactory + listener container factory
│
├── dataprovider/                       # output adapters (Spring lives here)
│   ├── InsertCustomerImpl.java          # @Component implements InsertCustomer
│   ├── FindCustomerByIdImpl.java        # @Component implements FindCustomerById
│   ├── UpdateCustomerImpl.java          # @Component implements UpdateCustomer
│   ├── DeleteCustomerByIdImpl.java      # @Component implements DeleteCustomerById
│   ├── FindAddressByZipCodeImp.java     # @Component implements FindAddressByZipCode
│   ├── client/
│   │   ├── FindAddressByZipCodeClient.java   # @FeignClient
│   │   ├── response/AddressResponse.java     # external DTO
│   │   └── mapper/AddressResponseMapper.java # @Mapper: AddressResponse → Address
│   └── repository/
│       ├── CustomerRepository.java           # @Repository extends MongoRepository
│       ├── entity/CustomerEntity.java        # @Document
│       ├── entity/AddressEntity.java         # @Document
│       └── mapper/CustomerEntityMapper.java  # @Mapper: Customer ↔ CustomerEntity
│
└── entrypoint/                         # input adapters (how the app is accessed)
    ├── controller/
    │   ├── CustomerController.java       # @RestController (/api/v1/customers)
    │   ├── request/CustomerRequest.java  # inbound DTO
    │   ├── respone/CustomerResponse.java # outbound DTO  (note: package typo "respone")
    │   ├── respone/AddressResponse.java
    │   └── mapper/CustomerMapper.java    # @Mapper: request/response ↔ Customer
    └── consumer/
        └── message/CustomerMessage.java  # Kafka message DTO (id, name, zipCode, cpf, isValidCpf)
```

## Layer ↔ component matrix

| Layer (ring)             | Package         | Components                                                                 |
|--------------------------|-----------------|----------------------------------------------------------------------------|
| Entities                 | `core.domain`   | `Customer`, `Address`                                                       |
| Use Cases                | `core.usecase`  | `*UseCase` interfaces + `*UseCaseImpl`                                       |
| Ports (boundaries)       | `core.dataprovider` | `InsertCustomer`, `FindCustomerById`, `UpdateCustomer`, `DeleteCustomerById`, `FindAddressByZipCode`, `SendCpfForValidation` |
| Interface Adapters (in)  | `entrypoint`    | `CustomerController`, Kafka consumer, DTOs + MapStruct mappers               |
| Interface Adapters (out) | `dataprovider`  | `*Impl` adapters, Feign client, Mongo repository, entities + mappers        |
| Frameworks & Drivers     | external        | MongoDB, address REST service, Kafka                                        |
| Wiring                   | `config`        | `@Bean` factories for use cases + Kafka producer/consumer config            |

## Class relationships

### Use case implementations → ports

| Use case impl                  | Depends on (constructor)                                              |
|--------------------------------|----------------------------------------------------------------------|
| `InsertCustomerUseCaseImpl`    | `FindAddressByZipCode`, `InsertCustomer`, `SendCpfForValidation`      |
| `FindCustomerByIdUseCaseImpl`  | `FindCustomerById`                                                    |
| `UpdateCustomerUseCaseImpl`    | `FindCustomerByIdUseCase`, `FindAddressByZipCode`, `UpdateCustomer`   |
| `DeleteCustomerByIdUseCaseImpl`| `FindCustomerByIdUseCase`, `DeleteCustomerById`                       |

> Note: `UpdateCustomer*` and `DeleteCustomerById*` impls reuse the **`FindCustomerByIdUseCase`**
> (an input port), not the raw `FindCustomerById` output port — they orchestrate over an existing
> use case to load the customer first.

### Ports → adapters (output)

| Port (`core.dataprovider`) | Adapter (`dataprovider`)   | Talks to                                            |
|----------------------------|----------------------------|-----------------------------------------------------|
| `InsertCustomer`           | `InsertCustomerImpl`       | `CustomerRepository` + `CustomerEntityMapper`       |
| `FindCustomerById`         | `FindCustomerByIdImpl`     | `CustomerRepository` + `CustomerEntityMapper`       |
| `UpdateCustomer`           | `UpdateCustomerImpl`       | `CustomerRepository` + `CustomerEntityMapper`       |
| `DeleteCustomerById`       | `DeleteCustomerByIdImpl`   | `CustomerRepository`                                |
| `FindAddressByZipCode`     | `FindAddressByZipCodeImp`  | `FindAddressByZipCodeClient` + `AddressResponseMapper` |
| `SendCpfForValidation`     | *(not yet implemented)*    | Kafka producer (planned)                            |

### Controller → use cases (input)

`CustomerController` depends on all four input ports:

```
CustomerController
 ├── InsertCustomerUseCase       POST   /api/v1/customers
 ├── FindCustomerByIdUseCase     GET    /api/v1/customers/{id}
 ├── UpdateCustomerUseCase       PUT    /api/v1/customers/{id}
 └── DeleteCustomerByIdUseCase   DELETE /api/v1/customers/{id}
```

### Spring wiring (`config`)

Because the use case impls are **not** Spring beans, each is built by an explicit `@Bean` factory
that receives the port adapters (injected by Spring) and constructs the impl:

```
InsertCustomerConfig      → new InsertCustomerUseCaseImpl(FindAddressByZipCode, InsertCustomer, ...)
FindCustomerByIdConfig    → new FindCustomerByIdUseCaseImpl(FindCustomerById)
UpdateCustomerConfig      → new UpdateCustomerUseCaseImpl(FindCustomerByIdUseCase, FindAddressByZipCode, UpdateCustomer)
DeleteCustomerByIdConfig  → new DeleteCustomerByIdUseCaseImpl(FindCustomerByIdUseCase, DeleteCustomerById)
KafkaProducerConfig       → ProducerFactory + KafkaTemplate
KafkaConsumerConfig       → ConsumerFactory + ConcurrentKafkaListenerContainerFactory
```

## End-to-end flow: create a customer

```
HTTP POST /api/v1/customers
   │
   ▼
CustomerController ──(CustomerMapper)──► Customer (domain)
   │
   ▼
InsertCustomerUseCase  (impl orchestrates)
   ├─► FindAddressByZipCode ─► FindAddressByZipCodeImp ─► Feign ─► address service
   │                                     └─(AddressResponseMapper)─► Address
   ├─► InsertCustomer ─► InsertCustomerImpl ─► CustomerRepository ─► MongoDB
   │                                  └─(CustomerEntityMapper)─► CustomerEntity
   └─► SendCpfForValidation ─► Kafka producer ─► validation topic
                                                       │
                                                  (external CPF validation API)
                                                       │
                                                       ▼
                                          Kafka consumer (CustomerMessage)
                                                       │
                                                       ▼
                                          UpdateCustomer ─► isValidCpf
```

## Notes / known gaps

- `SendCpfForValidationImpl` lives in `core` and is an **empty class that does not implement**
  `SendCpfForValidation`. A Kafka-backed adapter belongs in `dataprovider`, not `core` (which must
  stay framework-free).
- `InsertCustomerConfig` still builds `InsertCustomerUseCaseImpl` with **two** arguments, while the
  impl now requires **three** (`SendCpfForValidation` was added) — wiring needs updating.
- The Kafka **consumer listener** is not implemented yet; only the `CustomerMessage` DTO and the
  producer/consumer configuration exist.
- The response DTO package is spelled `respone` (typo) rather than `response`.
```
