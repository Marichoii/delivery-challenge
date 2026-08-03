# Delivery Challenge

Backend de uma aplicacao de delivery usando Java com Quarkus, com persistencia no IBM DB2 e publicacao de eventos via Kafka.

## O que tem agora

### Backend (`backend/order-service`)

- `GET /health`: verifica se a API esta rodando;
- `GET /menu`: lista os itens do cardapio a partir do banco de dados;
- `GET /customers`: retorna o cliente do MVP;
- `GET /restaurants`: retorna o restaurante do MVP;
- `POST /orders`: cria um pedido informando o `itemId`;
- `GET /orders`: lista todos os pedidos;
- `GET /orders/{id}`: busca um pedido pelo id;
- `GET /orders/{id}/history`: lista o historico de status do pedido;
- `POST /orders/{id}/confirm`: avanca o status para `CONFIRMED`;
- `POST /orders/{id}/prepare`: avanca o status para `PREPARING`;
- `POST /orders/{id}/ready`: avanca o status para `READY`;
- `POST /orders/{id}/deliver`: avanca o status para `DELIVERED`.

Os dados sao persistidos via JPA/Hibernate no IBM DB2 on Cloud. Nos testes, o backend usa H2 em memoria para nao depender do banco real.

O cliente, o restaurante e os itens do cardapio sao inseridos automaticamente na inicializacao pelo `MvpDataInitializer`. Para alterar qualquer dado fixo, edite esse arquivo e reinicie o Quarkus.

Cada pedido criado publica um evento no Kafka. A configuracao do Kafka so e ativada nos perfis `dev` e `prod` — nos testes ela e desligada.

## Como rodar o backend

Antes de subir o Quarkus, exporte as variaveis do DB2 no terminal:

```bash
export DB2_USERNAME=seu_usuario
export DB2_PASSWORD=sua_senha
export DB2_JDBC_URL='jdbc:db2://host-do-db2:porta/bludb:sslConnection=true;'
```

Para subir o Kafka localmente:

```bash
docker compose up -d
```

Para subir o backend:

```bash
cd backend/order-service
./mvnw quarkus:dev
```

Depois acesse:

- `http://localhost:8081/health`
- `http://localhost:8081/menu`
- `http://localhost:8081/orders`
- `http://localhost:8081/q/swagger-ui`

## Como rodar o frontend

Com o backend rodando, abra outro terminal:

```bash
cd frontend
npm install
npm start
```

Depois acesse `http://localhost:4200`.

## Exemplo de criacao de pedido

```bash
curl -X POST http://localhost:8081/orders \
  -H "Content-Type: application/json" \
  -d '{"itemId":"angus-divino"}'
```

Resposta:

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

## Exemplo de mudanca de status

```bash
curl -X POST http://localhost:8081/orders/ID_DO_PEDIDO/confirm
curl -X POST http://localhost:8081/orders/ID_DO_PEDIDO/prepare
curl -X POST http://localhost:8081/orders/ID_DO_PEDIDO/ready
curl -X POST http://localhost:8081/orders/ID_DO_PEDIDO/deliver
```

Para ver o historico:

```bash
curl http://localhost:8081/orders/ID_DO_PEDIDO/history
```

## Observacoes tecnicas sobre o DB2 on Cloud

### Schema do usuario

No IBM Db2 on Cloud, cada usuario tem seu proprio schema. O schema padrao do usuario nao e o mesmo nome do banco — e um codigo gerado pela IBM. Para confirmar os dados salvos pelo Quarkus, use o schema correto no Run SQL do console:

```sql
SELECT * FROM "SEU_SCHEMA".ORDERS
SELECT * FROM "SEU_SCHEMA".CUSTOMERS
SELECT * FROM "SEU_SCHEMA".RESTAURANTS
SELECT * FROM "SEU_SCHEMA".MENU_ITEMS
```

### Tipo da coluna ID

O DB2 nao aceita o tipo `UUID` do Java diretamente — ele converte para `CHAR(16) FOR BIT DATA`, causando `ERRORCODE=-4474`. A solucao foi mapear o campo `id` como `String` com `@Column(length = 36)` e gerar o UUID com `UUID.randomUUID().toString()`.

### Recriando tabelas com tipos corretos

Se uma tabela foi criada com tipo errado, o Hibernate com `strategy=update` nao consegue alterar a coluna `ID` por ser chave primaria. Para recriar, drope a tabela no console do IBM Cloud e reinicie o Quarkus — ele recria automaticamente.

### Variaveis de ambiente obrigatorias

O `application.properties` usa `${VARIAVEL}` sem fallback. As variaveis precisam estar no mesmo terminal onde o `./mvnw quarkus:dev` sera executado. Para persistir entre sessoes, adicione ao `.zshrc` ou use um arquivo `.env` com `source .env`.
