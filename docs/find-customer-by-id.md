# Feature em andamento: Buscar Cliente por ID

Este documento explica, de forma didática, o que foi construído até agora para a
funcionalidade de **buscar um cliente pelo seu ID**. São arquivos ainda **não commitados**
(em desenvolvimento), apresentados na ordem em que a Clean Architecture os organiza:
de dentro (regra de negócio) para fora (infraestrutura).

> Lembre-se da regra de ouro: o `core` não conhece o mundo externo. Quem depende é sempre
> a camada de fora. Por isso começamos pelo centro.

## Visão geral do fluxo

```
(entrypoint, ainda não criado)
   → FindCustomerByIdUseCase        (core: contrato do caso de uso)
       → FindCustomerByIdUseCaseImpl (core: regra, lança erro se não achar)
           → FindCustomerById        (core: porta de saída)
               → FindCustomerByIdImpl (dataprovider: adaptador Spring)
                   → CustomerRepository (MongoDB)
                   → CustomerEntityMapper (entity → domain)
```

## Arquivos criados

### 1. `core.dataprovider.FindCustomerById` (porta de saída)

```java
public interface FindCustomerById {
    Optional<Customer> find(final String id);
}
```

É a **porta**: um contrato que o `core` define para "sair" da aplicação e buscar dados,
sem saber *como* isso é feito (MongoDB, SQL, HTTP, etc.).

- Fala apenas em tipos de domínio (`Customer`), nunca em entidades do banco.
- Retorna `Optional<Customer>`: o cliente **pode não existir**, e isso é representado de forma
  explícita, sem `null`. A decisão sobre o que fazer quando não há resultado fica com o caso de uso.

### 2. `core.usecase.FindCustomerByIdUseCase` (contrato do caso de uso)

```java
public interface FindCustomerByIdUseCase {
    Customer find(final String id);
}
```

É a interface que o mundo externo (um controller, por exemplo) vai usar. Repare na diferença
em relação à porta: aqui o retorno é `Customer` direto, **não** `Optional`. A ausência de
resultado vira responsabilidade do caso de uso resolver.

### 3. `core.usecase.impl.FindCustomerByIdUseCaseImpl` (a regra de negócio)

```java
public class FindCustomerByIdUseCaseImpl implements FindCustomerByIdUseCase {

    private final FindCustomerById findCustomerById;

    public FindCustomerByIdUseCaseImpl(FindCustomerById findCustomerById) {
        this.findCustomerById = findCustomerById;
    }

    @Override
    public Customer find(String id) {
        return findCustomerById.find(id)
                .orElseThrow(() -> new RuntimeException("Customer not found"));
    }
}
```

Aqui está a regra: pede o cliente à porta e, **se não existir, lança uma exceção**
(`Customer not found`). Pontos importantes que mantêm o `core` livre de framework:

- **Injeção por construtor**, sem `@Autowired` e sem anotações do Spring.
- Depende apenas da **interface** `FindCustomerById`, não da implementação concreta. Quem vai
  fornecer a implementação real é a camada externa, na hora de montar a aplicação.

> Observação para o futuro: hoje o erro é um `RuntimeException` genérico. Um próximo passo
> natural é trocar por uma exceção de domínio (ex.: `CustomerNotFoundException`), que depois
> o entrypoint pode traduzir em um HTTP 404.

### 4. `dataprovider.FindCustomerByIdImpl` (o adaptador / infraestrutura)

```java
@Component
public class FindCustomerByIdImpl implements FindCustomerById {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private CustomerEntityMapper customerEntityMapper;

    @Override
    public Optional<Customer> find(String id) {
        var customerEntity = customerRepository.findById(id);
        return customerEntity.map(entity -> customerEntityMapper.toCustomer(entity));
    }
}
```

É o **adaptador**: a implementação concreta da porta, e o único lugar onde o Spring aparece.

- `@Component` registra o bean; `@Autowired` injeta o repositório e o mapper.
- Busca a `CustomerEntity` no MongoDB e a **converte para o domínio** (`Customer`) com o mapper.
  É exatamente na fronteira do `dataprovider` que entidade e domínio são separados.
- O `.map(...)` em cima do `Optional` preserva o "pode não existir" sem precisar de `if`.

### 5. `dataprovider.repository.mapper.CustomerEntityMapper` (alteração)

```diff
- CustomerEntity toCustomerEntity(Customer costomer);
+ CustomerEntity toCustomerEntity(Customer customer);
+
+ Customer toCustomer(CustomerEntity customerEntity);
```

O mapper (MapStruct) ganhou o caminho **inverso**: antes só convertia domínio → entidade
(para salvar); agora também converte entidade → domínio (`toCustomer`), necessário para a
leitura. De quebra, corrigiu-se o typo `costomer` → `customer`.

## O que ainda falta

- **Entrypoint**: um endpoint REST (ex.: `GET /api/v1/customers/{id}`) no `CustomerController`
  para expor o caso de uso.
- **Wiring**: como os `*UseCaseImpl` não são beans do Spring, é preciso uma `@Configuration`
  com `@Bean` que construa o `FindCustomerByIdUseCaseImpl` injetando a porta.
- **Tratamento de erro**: substituir o `RuntimeException` por uma exceção de domínio e mapeá-la
  para um status HTTP adequado.

## Por que isso é Clean Architecture na prática

Cada peça tem um único motivo para mudar e as dependências apontam sempre para o centro:

| Camada | Arquivo | Conhece framework? |
|--------|---------|--------------------|
| Porta (core) | `FindCustomerById` | Não |
| Caso de uso (core) | `FindCustomerByIdUseCase(Impl)` | Não |
| Adaptador (dataprovider) | `FindCustomerByIdImpl` | Sim (Spring) |
| Mapper (dataprovider) | `CustomerEntityMapper` | Sim (MapStruct) |

Trocar o MongoDB por outro banco amanhã só afeta o adaptador e o mapper. O caso de uso e a
porta, que carregam a regra de negócio, permanecem intactos.
