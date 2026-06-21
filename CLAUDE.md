# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Commands

Build tool is the Gradle wrapper (`./gradlew`). Java 21 toolchain, Spring Boot 4.1.0, Spring Cloud 2025.0.0.

- Build: `./gradlew build`
- Run the app: `./gradlew bootRun`
- Run all tests: `./gradlew test`
- Run a single test class: `./gradlew test --tests 'com.paschoalick.cleanarch.CleanarchApplicationTests'`
- Run a single test method: `./gradlew test --tests 'com.paschoalick.cleanarch.CleanarchApplicationTests.contextLoads'`

`./gradlew test` (and `build`) boots a Spring context (`@SpringBootTest`), so an external MongoDB and the address Feign endpoint configured in `application.yml` may be required for some tests to pass.

## Runtime dependencies

`application.yml` expects:
- MongoDB at `mongodb://root:example@localhost:27017/cleanarch` (auth source `admin`)
- The address-lookup REST service at `paschoalick.client.address.url` (default `http://localhost:8082/address`), consumed via OpenFeign

## Architecture

This is a Clean Architecture demo. The package layout enforces the dependency rule: **`core` must not depend on `dataprovider` or any framework; `dataprovider` depends on `core`.**

### `core` — framework-free business layer
- `core.domain` — plain Java domain objects (`Customer`, `Address`). These are intentionally hand-written POJOs with no Lombok and no Spring annotations (see the Portuguese comments in `Customer.java`) so the domain stays library-free.
- `core.dataprovider` — **ports**: interfaces the use cases depend on (`InsertCustomer`, `FindAddressByZipCode`). They speak only in domain types.
- `core.usecase` — use case interface (`InsertCustomerUseCase`) + `core.usecase.impl` implementation. `InsertCustomerUseCaseImpl` uses **constructor injection only, no `@Autowired` / no Spring annotations** — also deliberate, to keep the use case layer framework-free.

### `dataprovider` — infrastructure adapters (Spring lives here)
Implements the `core.dataprovider` ports and is where all framework code is allowed:
- `dataprovider.InsertCustomerImpl` / `FindAddressByZipCodeImp` — `@Component` adapters implementing the core ports.
- `dataprovider.client` — OpenFeign client (`FindAddressByZipCodeClient`) + its `response` DTO and a MapStruct `mapper` that converts the external `AddressResponse` into the domain `Address`.
- `dataprovider.repository` — Spring Data MongoDB `CustomerRepository` and `entity` documents (`CustomerEntity`, `AddressEntity`). Entities are separate from `core.domain` types and mapped at this boundary.

### Conventions to preserve when extending
- New domain types and use cases go under `core` and must stay free of Spring/Lombok/MapStruct imports. Lombok (`@Data`) and Spring annotations belong only to `dataprovider` adapters, entities, and DTOs.
- Each external concern (DB, HTTP) is reached through a `core.dataprovider` interface; add the interface in `core` and its `@Component` implementation in `dataprovider`.
- Because use case impls are not Spring beans, wiring them (and supplying their port dependencies) requires an explicit `@Configuration`/`@Bean` factory — note there is not yet one in the codebase, nor a web/controller entrypoint layer; the project is a work in progress.
