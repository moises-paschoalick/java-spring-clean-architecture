# Clean Architecture

Ler:

[https://medium.com/luizalabs/descomplicando-a-clean-architecture-cf4dfc4a1ac6](https://medium.com/luizalabs/descomplicando-a-clean-architecture-cf4dfc4a1ac6)

Ucle Bob

Uma maneira mais simples de organizar o código e mais reutilizável

Deixar o core da aplicação totalmente isolado do mundo externo

- Arquitetura Hexagonal
- Onnion Architecture

Deixar o CORE da aplicação isolado do mundo externo

![image.png](Clean%20Architecture/image.png)

Entities → aqui são as classes de domínio (objetos de negócio da aplicação)

Casos de Usos → Regras de negócio específicas (controle de fluxos e etc)

Em verde → Adaptadores de interface, é a camada de comunicação entre o mundo interno e externo em azul

Em azul é o mundo externo

O acesso sempre é de fora para dentro → →

Dificuldade de aplicar o Clean Architeture

![image.png](Clean%20Architecture/image%201.png)

Separação em pasta:

```bash
core
-- dataprovider (interface de como vai sair do core da aplicação)
   -- cliente para acessar outro microservico
   -- inteface de saida do core
-- domain (classes de dominio)
-- usecase (casos de uso - regras de negócios)

-- dataprovider (interaces de saida)
   -- client
   -- repository
-- entrypoint (tudo como é fetio o acesso a aplicao)
   -- controller
   -- consumer (kafka)
```

Comparação do Spring

Padrão camadas

![image.png](Clean%20Architecture/image%202.png)

```bash
Entity (model)
DTOS
Controllers
Services
Repository
```

core.domain em:
POJO -> Plain Old Java Object (Objeto Java Simples e Antigo)
É uma classe Java comum, sem amarração a nenhum framework, especificação ou interface especial. 