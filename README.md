# Delivery Challenge

Aplicação simples de delivery para praticar backend, API REST, persistência, frontend e integração por eventos.

O objetivo não é construir uma plataforma completa. O escopo atual cobre um fluxo pequeno com cliente, restaurante, cardápio, criação de pedidos e evolução do status do pedido.

## Stack

- Backend: Java 21, Quarkus, JPA/Hibernate, REST Jackson e OpenAPI.
- Banco de dados: IBM DB2 em desenvolvimento/produção e H2 nos testes.
- Mensageria: Kafka local via Docker Compose.
- Frontend: Angular 17.
- CI: GitHub Actions com build do backend e do frontend.

## Estrutura

```text
.
├── backend/order-service   # API Quarkus
├── frontend                # Aplicação Angular
├── docker-compose.yml      # Kafka local
└── .github/workflows       # CI do projeto
```

## Funcionalidades Atuais

- Listagem de cliente, restaurante e cardápio do MVP.
- Criação de pedidos a partir de um `itemId`.
- Persistência de pedidos, histórico e status no DB2.
- Avanço do pedido pelas etapas `CREATED`, `CONFIRMED`, `PREPARING`, `READY` e `DELIVERED`.
- Publicação e consumo de eventos de pedido via Kafka nos perfis `dev` e `prod`.
- Frontend com visão de cliente e painel operacional do restaurante.
- Testes automatizados do backend usando H2 em memória.

## Pré-Requisitos

- Java 21.
- Maven Wrapper, já incluído em `backend/order-service`.
- Node.js 20 ou compatível com Angular 17.
- Docker, para subir o Kafka local.
- Credenciais do IBM DB2, para rodar o backend fora do perfil de teste.

## Variáveis de Ambiente

Antes de subir o backend em modo dev, exporte as variáveis do DB2:

```bash
export DB2_USERNAME=seu_usuario
export DB2_PASSWORD=sua_senha
export DB2_JDBC_URL='jdbc:db2://host-do-db2:porta/bludb:sslConnection=true;'
```

Opcionalmente, configure outro broker Kafka:

```bash
export KAFKA_BOOTSTRAP_SERVERS=localhost:9092
```

## Como Rodar

Suba o Kafka local:

```bash
docker compose up -d
```

Suba o backend:

```bash
cd backend/order-service
./mvnw quarkus:dev
```

A API ficará disponível em:

- `http://localhost:8081/health`
- `http://localhost:8081/menu`
- `http://localhost:8081/orders`
- `http://localhost:8081/q/swagger-ui`

Em outro terminal, suba o frontend:

```bash
cd frontend
npm install
npm start
```

Acesse `http://localhost:4200`.

## Endpoints Principais

| Método | Rota | Descrição |
| --- | --- | --- |
| `GET` | `/health` | Verifica se a API está online. |
| `GET` | `/customers` | Lista o cliente do MVP. |
| `GET` | `/restaurants` | Lista o restaurante do MVP. |
| `GET` | `/menu` | Lista os itens do cardápio. |
| `POST` | `/orders` | Cria um pedido. |
| `GET` | `/orders` | Lista pedidos. |
| `GET` | `/orders/{id}` | Busca um pedido por ID. |
| `GET` | `/orders/{id}/history` | Lista o histórico do pedido. |
| `POST` | `/orders/{id}/confirm` | Avança para `CONFIRMED`. |
| `POST` | `/orders/{id}/prepare` | Avança para `PREPARING`. |
| `POST` | `/orders/{id}/ready` | Avança para `READY`. |
| `POST` | `/orders/{id}/deliver` | Avança para `DELIVERED`. |

## Exemplos

Criar pedido:

```bash
curl -X POST http://localhost:8081/orders \
  -H "Content-Type: application/json" \
  -d '{"itemId":"angus-divino"}'
```

Resposta esperada:

```json
{
  "id": "...",
  "customerId": "cliente-mvp",
  "restaurantId": "restaurante-mvp",
  "itemId": "angus-divino",
  "itemName": "Angus Divino",
  "price": 120.0,
  "status": "CREATED"
}
```

Avançar status:

```bash
curl -X POST http://localhost:8081/orders/ID_DO_PEDIDO/confirm
curl -X POST http://localhost:8081/orders/ID_DO_PEDIDO/prepare
curl -X POST http://localhost:8081/orders/ID_DO_PEDIDO/ready
curl -X POST http://localhost:8081/orders/ID_DO_PEDIDO/deliver
```

Consultar histórico:

```bash
curl http://localhost:8081/orders/ID_DO_PEDIDO/history
```

## Testes e Build

Backend:

```bash
cd backend/order-service
./mvnw test
```

Frontend:

```bash
cd frontend
npm ci
npm run build
```

## Observações Sobre o DB2

No IBM DB2 on Cloud, cada usuário tem seu próprio schema. Para conferir os dados no console SQL, use o schema correto do usuário:

```sql
SELECT * FROM "SEU_SCHEMA".ORDERS;
SELECT * FROM "SEU_SCHEMA".ORDER_HISTORY;
SELECT * FROM "SEU_SCHEMA".CUSTOMERS;
SELECT * FROM "SEU_SCHEMA".RESTAURANTS;
SELECT * FROM "SEU_SCHEMA".MENU_ITEMS;
```

O DB2 não aceita o tipo `UUID` do Java diretamente nesse mapeamento. Por isso, os IDs são armazenados como `String` com `@Column(length = 36)`.

Se uma tabela foi criada com tipo incorreto, o Hibernate com `schema-management.strategy=update` não altera a chave primária automaticamente. Nesse caso, remova a tabela pelo console do DB2 e reinicie o Quarkus para recriá-la.
