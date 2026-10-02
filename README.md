# GS Flex
## Sistema de Controle de Pedidos e Transportadoras

Sistema web desenvolvido para controle de pedidos contabilizados por transportadoras, permitindo registrar diariamente a quantidade de pedidos por modalidade, acompanhar valores a receber e gerar relatórios quinzenais em PDF.

O projeto será desenvolvido utilizando **Java + Spring Boot** no backend, com persistência em **PostgreSQL** e uma interface web desenvolvida posteriormente.

---

## 📌 Objetivo

O sistema tem como objetivo facilitar o controle diário de pedidos realizados por diferentes transportadoras.

Para cada dia, será possível registrar:

* Quantidade de pedidos da Shopee;
* Quantidade de pedidos do Mercado Livre;
* Quantidade de pedidos Avulsos;
* Transportadora responsável.

A partir desses dados, o sistema calculará automaticamente:

* Total de pedidos;
* Total de pedidos por modalidade;
* Valor a receber por modalidade;
* Valor total a receber;
* Totais por transportadora;
* Totais de uma quinzena;
* Histórico de períodos anteriores.

Também será possível gerar um **relatório em PDF por transportadora**.

---

# 🎯 Funcionalidades

## Transportadoras

O sistema permitirá:

* Cadastrar transportadora;
* Listar transportadoras;
* Consultar uma transportadora;
* Editar transportadora;
* Excluir transportadora.

Cada transportadora possuirá:

```text
Nome
Valor por pedido Shopee
Valor por pedido Mercado Livre
Valor por pedido Avulso
```

Exemplo:

```text
Transportadora: Jadlog

Shopee:        R$ 2,50
Mercado Livre: R$ 3,00
Avulso:        R$ 4,00
```

---

## 📦 Registro diário de pedidos

Para cada transportadora, será possível registrar a quantidade de pedidos de determinado dia.

Exemplo:

```text
Data: 01/10/2026

Transportadora: Jadlog

Shopee:        20
Mercado Livre: 15
Avulsos:        5
```

O sistema calculará:

```text
Shopee:
20 × R$ 2,50 = R$ 50,00

Mercado Livre:
15 × R$ 3,00 = R$ 45,00

Avulsos:
5 × R$ 4,00 = R$ 20,00

Total:
R$ 115,00
```

---

## ✏️ Alteração de registros

O usuário poderá selecionar uma data e alterar os registros existentes.

Exemplo:

```text
01/10/2026

Jadlog
Shopee: 20
ML: 15
Avulso: 5
```

Caso a quantidade da Shopee esteja incorreta:

```text
Shopee: 25
```

Após salvar, o registro existente será atualizado.

O sistema não deverá criar registros duplicados para a mesma transportadora e data.

---

# 📊 Dashboard

O sistema possuirá um dashboard para visualização dos dados de determinado período.

O usuário poderá selecionar um período, por exemplo:

```text
01/10/2026 → 15/10/2026
```

O dashboard apresentará:

### Quantidade de pedidos

```text
Total de pedidos
Total Shopee
Total Mercado Livre
Total Avulso
```

### Valores

```text
Total Shopee
Total Mercado Livre
Total Avulso
Total a receber
```

### Por transportadora

```text
Transportadora     Pedidos       Valor

Jadlog               500        R$ 1.500,00
Loggi                350        R$ 1.100,00
Total Express        250        R$   800,00
```

---

# 📄 Relatório PDF

Será possível selecionar uma transportadora e gerar um PDF referente a determinado período.

Exemplo:

```text
Transportadora: Jadlog
Período: 01/10/2026 até 15/10/2026
```

O relatório deverá apresentar os registros de cada dia:

```text
DATA       SHOPEE    ML    AVULSO    TOTAL

01/10        20      15       5      R$ 115,00
02/10        25      10       3      R$ 112,50
03/10        18      20       4      R$ 129,00
...
15/10
```

Depois deverá apresentar:

```text
TOTAL SHOPEE
Quantidade: 250
Valor: R$ 625,00

TOTAL MERCADO LIVRE
Quantidade: 180
Valor: R$ 540,00

TOTAL AVULSO
Quantidade: 50
Valor: R$ 200,00

TOTAL DE PEDIDOS
480

TOTAL A RECEBER
R$ 1.365,00
```

---

# 🗄️ Persistência e histórico

Os dados deverão permanecer armazenados permanentemente.

O sistema deverá permitir consultar períodos antigos.

Exemplo:

```text
01/09/2026 → 15/09/2026
16/09/2026 → 30/09/2026

01/10/2026 → 15/10/2026
16/10/2026 → 31/10/2026
```

Não será criada uma tabela específica para cada quinzena.

Os registros serão armazenados individualmente por data.

A quinzena será determinada através de um intervalo:

```text
dataInicial
dataFinal
```

Isso também permite consultar períodos personalizados.

---

# ⚠️ Histórico dos valores

Os valores das transportadoras podem ser alterados no futuro.

Por isso, o sistema deverá preservar o valor utilizado no momento do lançamento.

Exemplo:

### Outubro

```text
Shopee = R$ 2,50
```

Registro:

```text
10 pedidos × R$ 2,50
= R$ 25,00
```

Posteriormente, a transportadora altera seu preço:

```text
Shopee = R$ 3,00
```

Os registros antigos **não deverão ser alterados**.

Por isso, o `RegistroDiario` armazenará tanto as quantidades quanto os valores utilizados naquele momento.

---

# 🏗️ Arquitetura

O projeto utilizará uma arquitetura baseada em camadas.

```text
                 FRONTEND
                    │
                    ▼
               CONTROLLER
                    │
                    ▼
                 SERVICE
                    │
                    ▼
               REPOSITORY
                    │
                    ▼
                POSTGRESQL
```

Estrutura geral:

```text
src/
└── main/
    ├── java/
    │   └── com.seuprojeto.pedidos/
    │
    │       ├── controller/
    │       │
    │       ├── service/
    │       │
    │       ├── repository/
    │       │
    │       ├── entity/
    │       │
    │       ├── dto/
    │       │   ├── request/
    │       │   └── response/
    │       │
    │       ├── exception/
    │       │
    │       ├── mapper/
    │       │
    │       └── config/
    │
    └── resources/
        └── application.properties
```

---

# 📁 Estrutura das camadas

## Controller

Responsável por receber as requisições HTTP e devolver as respostas.

```text
controller/
├── TransportadoraController
├── RegistroDiarioController
├── DashboardController
└── RelatorioController
```

O Controller não deverá concentrar regras de negócio.

Fluxo:

```text
HTTP Request
     ↓
Controller
     ↓
Service
```

---

## Service

Responsável pelas regras de negócio.

```text
service/
├── TransportadoraService
├── RegistroDiarioService
├── DashboardService
└── RelatorioService
```

Exemplos de responsabilidades:

### TransportadoraService

* Criar transportadora;
* Buscar transportadora;
* Listar transportadoras;
* Atualizar transportadora;
* Excluir transportadora.

### RegistroDiarioService

* Criar registro;
* Atualizar registro;
* Consultar registros;
* Validar transportadora;
* Validar duplicidade;
* Preservar os valores históricos.

### DashboardService

* Calcular totais;
* Consultar períodos;
* Agrupar informações;
* Calcular valores por transportadora.

### RelatorioService

* Buscar informações;
* Calcular totais;
* Montar dados do relatório;
* Gerar PDF.

---

# 📁 Repository

Responsável pelo acesso ao banco através do Spring Data JPA.

```text
repository/
├── TransportadoraRepository
└── RegistroDiarioRepository
```

Os repositories serão responsáveis pelas consultas e operações de persistência.

Exemplos:

```text
findAll()
findById()
save()
delete()
findByData()
findByDataBetween()
findByTransportadoraId()
```

---

# 📁 Entity

Representam as tabelas do banco de dados.

```text
entity/
├── Transportadora
└── RegistroDiario
```

---

# 🚚 Entity: Transportadora

Campos:

```text
id
nome
valorShopee
valorMercadoLivre
valorAvulso
```

Representação:

```text
TRANSPORTADORA

id
nome
valor_shopee
valor_mercado_livre
valor_avulso
```

---

# 📦 Entity: RegistroDiario

Campos:

```text
id
data
transportadora

quantidadeShopee
quantidadeMercadoLivre
quantidadeAvulso

valorShopee
valorMercadoLivre
valorAvulso
```

Representação:

```text
REGISTRO_DIARIO

id
data
transportadora_id

quantidade_shopee
quantidade_mercado_livre
quantidade_avulso

valor_shopee
valor_mercado_livre
valor_avulso
```

---

# 🔗 Relacionamento

Uma transportadora poderá possuir vários registros diários.

```text
TRANSPORTADORA
       │
       │ 1
       │
       │ N
       ▼
REGISTRO_DIARIO
```

Ou:

```text
Uma Transportadora
        ↓
Pode possuir
        ↓
Vários RegistrosDiarios
```

---

# 🔒 Regra de unicidade

Não poderá existir mais de um registro para a mesma transportadora no mesmo dia.

Exemplo inválido:

```text
Jadlog | 01/10/2026
Jadlog | 01/10/2026
```

A combinação:

```text
transportadora_id + data
```

deverá ser única.

Essa regra será implementada também no banco de dados.

---

# 📁 DTO

Os DTOs serão utilizados para controlar os dados recebidos e enviados pela API.

Estrutura:

```text
dto/
├── request/
│   ├── TransportadoraRequest
│   └── RegistroDiarioRequest
│
└── response/
    ├── TransportadoraResponse
    ├── RegistroDiarioResponse
    └── DashboardResponse
```

---

# 📥 TransportadoraRequest

Dados recebidos para criação ou alteração:

```json
{
  "nome": "Jadlog",
  "valorShopee": 2.50,
  "valorMercadoLivre": 3.00,
  "valorAvulso": 4.00
}
```

---

# 📥 RegistroDiarioRequest

Dados recebidos para criação ou alteração:

```json
{
  "data": "2026-10-01",
  "transportadoraId": 1,
  "quantidadeShopee": 20,
  "quantidadeMercadoLivre": 15,
  "quantidadeAvulso": 5
}
```

Os valores não precisam ser enviados pelo frontend.

O backend deverá buscar os valores da transportadora no momento da criação do registro.

---

# 📤 DTOs de Response

Os responses representarão os dados devolvidos pela API.

Exemplo:

```json
{
  "id": 1,
  "transportadora": "Jadlog",
  "data": "2026-10-01",
  "quantidadeShopee": 20,
  "quantidadeMercadoLivre": 15,
  "quantidadeAvulso": 5,
  "valorShopee": 2.50,
  "valorMercadoLivre": 3.00,
  "valorAvulso": 4.00,
  "total": 115.00
}
```

---

# 📁 Exception

Estrutura:

```text
exception/
├── ResourceNotFoundException
├── BusinessException
└── GlobalExceptionHandler
```

Exemplos de erros:

```text
Transportadora não encontrada
Registro não encontrado
Registro já existe para essa data
Quantidade não pode ser negativa
```

As exceções serão convertidas em respostas HTTP adequadas.

Exemplo:

```json
{
  "status": 404,
  "message": "Transportadora não encontrada"
}
```

---

# 📁 Mapper

Responsável por converter objetos.

```text
Entity → Response DTO
Request DTO → Entity
```

Exemplo:

```text
Transportadora
      ↓
TransportadoraResponse
```

---

# 🗃️ Banco de dados

O banco utilizado será:

**PostgreSQL**

Modelo inicial:

```text
┌────────────────────────────┐
│       TRANSPORTADORA       │
├────────────────────────────┤
│ id PK                      │
│ nome                       │
│ valor_shopee               │
│ valor_mercado_livre        │
│ valor_avulso               │
└──────────────┬─────────────┘
               │
               │ 1:N
               │
               ▼
┌────────────────────────────┐
│       REGISTRO_DIARIO      │
├────────────────────────────┤
│ id PK                      │
│ data                       │
│ transportadora_id FK       │
│ quantidade_shopee          │
│ quantidade_mercado_livre   │
│ quantidade_avulso          │
│ valor_shopee               │
│ valor_mercado_livre        │
│ valor_avulso               │
└────────────────────────────┘
```

---

# 🐳 Docker

O PostgreSQL será executado através do Docker.

Exemplo de `docker-compose.yml`:

```yaml
services:

  postgres:
    image: postgres:16
    container_name: pedidos-postgres

    environment:
      POSTGRES_DB: pedidos
      POSTGRES_USER: postgres
      POSTGRES_PASSWORD: postgres

    ports:
      - "5432:5432"

    volumes:
      - postgres_data:/var/lib/postgresql/data

volumes:
  postgres_data:
```

O volume garante a persistência dos dados mesmo quando o container for parado.

---

# 🔌 API

## Transportadoras

### Criar

```http
POST /api/transportadoras
```

Request:

```json
{
  "nome": "Jadlog",
  "valorShopee": 2.50,
  "valorMercadoLivre": 3.00,
  "valorAvulso": 4.00
}
```

---

### Listar

```http
GET /api/transportadoras
```

---

### Buscar por ID

```http
GET /api/transportadoras/{id}
```

---

### Atualizar

```http
PUT /api/transportadoras/{id}
```

---

### Excluir

```http
DELETE /api/transportadoras/{id}
```

---

# 📦 Registros

### Criar registro

```http
POST /api/registros
```

Request:

```json
{
  "data": "2026-10-01",
  "transportadoraId": 1,
  "quantidadeShopee": 20,
  "quantidadeMercadoLivre": 15,
  "quantidadeAvulso": 5
}
```

---

### Buscar registro

```http
GET /api/registros/{id}
```

---

### Atualizar registro

```http
PUT /api/registros/{id}
```

---

### Buscar registros por data

```http
GET /api/registros?data=2026-10-01
```

---

### Buscar registros por período

```http
GET /api/registros?inicio=2026-10-01&fim=2026-10-15
```

---

# 📊 Dashboard

Endpoint:

```http
GET /api/dashboard
```

Parâmetros:

```text
inicio
fim
```

Exemplo:

```http
GET /api/dashboard?inicio=2026-10-01&fim=2026-10-15
```

Resposta esperada:

```json
{
  "periodo": {
    "inicio": "2026-10-01",
    "fim": "2026-10-15"
  },
  "totalPedidos": 1250,
  "quantidadeShopee": 600,
  "quantidadeMercadoLivre": 500,
  "quantidadeAvulso": 150,
  "totalShopee": 1500.00,
  "totalMercadoLivre": 1600.00,
  "totalAvulso": 600.00,
  "totalReceber": 3700.00
}
```

---

# 📄 Relatório

Endpoint planejado:

```http
GET /api/relatorios/transportadora/{id}
```

Parâmetros:

```text
inicio
fim
```

Exemplo:

```http
GET /api/relatorios/transportadora/1?inicio=2026-10-01&fim=2026-10-15
```

Response:

```text
application/pdf
```

O PDF deverá conter:

* Nome da transportadora;
* Período;
* Pedidos de cada dia;
* Quantidade Shopee;
* Quantidade Mercado Livre;
* Quantidade Avulso;
* Valor diário;
* Total Shopee;
* Total Mercado Livre;
* Total Avulso;
* Total de pedidos;
* Total a receber.

---

# 💰 Regras de cálculo

Para cada modalidade:

```text
Valor Shopee =
Quantidade Shopee × Valor Shopee
```

```text
Valor Mercado Livre =
Quantidade Mercado Livre × Valor Mercado Livre
```

```text
Valor Avulso =
Quantidade Avulso × Valor Avulso
```

Total:

```text
Total =
Valor Shopee
+
Valor Mercado Livre
+
Valor Avulso
```

Quantidade total:

```text
Total de pedidos =
Shopee
+
Mercado Livre
+
Avulso
```

---

# 📅 Controle de quinzenas

A aplicação não terá necessariamente uma entidade `Quinzena`.

Uma quinzena será representada por um intervalo de datas.

### Primeira quinzena

```text
01 → 15
```

### Segunda quinzena

```text
16 → último dia do mês
```

Exemplo:

```text
01/10/2026 → 15/10/2026
```

ou:

```text
16/10/2026 → 31/10/2026
```

Também será possível consultar períodos personalizados.

---

# 🖥️ Frontend

O frontend será desenvolvido posteriormente.

A interface deverá priorizar:

* Simplicidade;
* Facilidade de uso;
* Poucos cliques;
* Visualização clara;
* Responsividade;
* Feedback após salvar;
* Facilidade para editar registros;
* Seleção rápida de datas;
* Dashboard visual.

Uma possível tela de lançamento:

```text
┌─────────────────────────────────────────────┐
│             LANÇAMENTO DE PEDIDOS           │
├─────────────────────────────────────────────┤
│                                             │
│ Data: [ 01/10/2026 ▼ ]                     │
│                                             │
│ Transportadora   Shopee   ML   Avulso      │
│                                             │
│ Jadlog              20     15      5        │
│ Loggi               10     12      2        │
│ Total Express        8      7      3        │
│                                             │
│                  [ SALVAR ]                 │
│                                             │
└─────────────────────────────────────────────┘
```

---

# 🛠️ Tecnologias

## Backend

* Java 17+
* Spring Boot
* Spring Web
* Spring Data JPA
* Bean Validation
* Lombok
* Maven

## Banco

* PostgreSQL

## Infraestrutura

* Docker
* Docker Compose

## Testes

Planejado:

* JUnit
* Mockito
* Testes de integração

## Frontend

Será definido posteriormente.

Possíveis opções:

* React;
* HTML/CSS/JavaScript;
* Outra tecnologia conforme necessidade do projeto.

---

# 🧪 Testes

O backend deverá possuir testes para as principais regras.

Exemplos:

```text
✓ Criar transportadora
✓ Editar transportadora
✓ Excluir transportadora
✓ Buscar transportadora
✓ Criar registro diário
✓ Editar registro diário
✓ Impedir registro duplicado
✓ Impedir quantidade negativa
✓ Calcular valor corretamente
✓ Consultar período
✓ Calcular dashboard
✓ Gerar relatório
```

---

# 🔐 Segurança

A primeira versão poderá funcionar sem autenticação caso apenas uma pessoa utilize o sistema.

Posteriormente poderá ser implementado:

```text
Spring Security
JWT
Login
Usuários
Permissões
```

Exemplo futuro:

```text
ADMIN
    ↓
Pode cadastrar/excluir transportadoras

OPERADOR
    ↓
Pode lançar e editar pedidos
```

Essa funcionalidade não faz parte do MVP inicial.

---

# 📈 Roadmap

## Fase 1 — Configuração

* [X] Criar projeto Spring Boot
* [X] Configurar Maven
* [X] Configurar PostgreSQL
* [X] Configurar Docker
* [X] Configurar `application.properties`

---

## Fase 2 — Modelagem

* [X] Criar `Transportadora`
* [X] Criar `RegistroDiario`
* [X] Criar relacionamento
* [ ] Criar constraints
* [ ] Criar repositories

---

## Fase 3 — Transportadoras

* [ ] Criar DTOs
* [ ] Criar Service
* [ ] Criar Controller
* [ ] Implementar cadastro
* [ ] Implementar listagem
* [ ] Implementar consulta
* [ ] Implementar edição
* [ ] Implementar exclusão
* [ ] Testar no Postman

---

## Fase 4 — Registros

* [ ] Criar DTOs
* [ ] Criar Service
* [ ] Criar Controller
* [ ] Criar registro
* [ ] Editar registro
* [ ] Consultar por data
* [ ] Consultar por período
* [ ] Validar duplicidade
* [ ] Validar quantidades
* [ ] Testar no Postman

---

## Fase 5 — Dashboard

* [ ] Criar `DashboardService`
* [ ] Criar `DashboardResponse`
* [ ] Criar consultas
* [ ] Calcular quantidade total
* [ ] Calcular valores
* [ ] Calcular totais por transportadora
* [ ] Criar endpoint
* [ ] Testar no Postman

---

## Fase 6 — PDF

* [ ] Escolher biblioteca
* [ ] Criar `RelatorioService`
* [ ] Criar layout do relatório
* [ ] Adicionar registros diários
* [ ] Adicionar totais
* [ ] Gerar PDF
* [ ] Testar download

---

## Fase 7 — Frontend

* [ ] Definir tecnologia
* [ ] Criar layout
* [ ] Criar dashboard
* [ ] Criar tela de lançamento
* [ ] Criar tela de transportadoras
* [ ] Criar tela de histórico
* [ ] Criar tela de relatórios
* [ ] Integrar com API
* [ ] Responsividade
* [ ] Testes de usabilidade

---

## Fase 8 — Finalização

* [ ] Tratamento de erros
* [ ] Validações
* [ ] Testes automatizados
* [ ] Documentação da API
* [ ] Swagger/OpenAPI
* [ ] Melhorias de UX
* [ ] Dockerização completa
* [ ] Deploy

---

# 📌 MVP

A primeira versão funcional deverá possuir somente:

```text
Transportadoras
        ↓
Cadastro / Edição / Exclusão
        ↓
Registros Diários
        ↓
Consulta / Edição
        ↓
Dashboard
        ↓
Relatório PDF
```

O foco inicial será garantir:

1. **Os dados serem salvos corretamente;**
2. **Os dados históricos nunca serem perdidos;**
3. **Os cálculos serem confiáveis;**
4. **Os registros poderem ser alterados;**
5. **As consultas por período funcionarem corretamente.**

Depois disso, a interface será construída sobre a API.

---

# 📂 Arquitetura final esperada

```text
sistema-pedidos/
│
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com.seuprojeto.pedidos/
│   │   │
│   │   │       ├── controller/
│   │   │       │   ├── TransportadoraController
│   │   │       │   ├── RegistroDiarioController
│   │   │       │   ├── DashboardController
│   │   │       │   └── RelatorioController
│   │   │       │
│   │   │       ├── service/
│   │   │       │   ├── TransportadoraService
│   │   │       │   ├── RegistroDiarioService
│   │   │       │   ├── DashboardService
│   │   │       │   └── RelatorioService
│   │   │       │
│   │   │       ├── repository/
│   │   │       │   ├── TransportadoraRepository
│   │   │       │   └── RegistroDiarioRepository
│   │   │       │
│   │   │       ├── entity/
│   │   │       │   ├── Transportadora
│   │   │       │   └── RegistroDiario
│   │   │       │
│   │   │       ├── dto/
│   │   │       │   ├── request/
│   │   │       │   │   ├── TransportadoraRequest
│   │   │       │   │   └── RegistroDiarioRequest
│   │   │       │   │
│   │   │       │   └── response/
│   │   │       │       ├── TransportadoraResponse
│   │   │       │       ├── RegistroDiarioResponse
│   │   │       │       └── DashboardResponse
│   │   │       │
│   │   │       ├── exception/
│   │   │       │   ├── ResourceNotFoundException
│   │   │       │   ├── BusinessException
│   │   │       │   └── GlobalExceptionHandler
│   │   │       │
│   │   │       ├── mapper/
│   │   │       │
│   │   │       └── config/
│   │   │
│   │   └── resources/
│   │       └── application.properties
│   │
│   └── test/
│
├── docker-compose.yml
├── pom.xml
├── README.md
└── .gitignore
```

---

# 🚀 Próximo passo

O desenvolvimento deverá começar pela infraestrutura e modelagem:

```text
1. Criar projeto Spring Boot
2. Configurar PostgreSQL
3. Configurar Docker
4. Criar Entity Transportadora
5. Criar Entity RegistroDiario
6. Criar relacionamento
7. Criar Repository
```

Somente depois disso serão desenvolvidos os Controllers e Services.

A aplicação será construída **incrementalmente**, validando cada parte no Postman antes de avançar para a próxima etapa.
