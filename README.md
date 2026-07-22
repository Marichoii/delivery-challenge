# Delivery Challenge

Inicio bem simples do backend de uma aplicacao de delivery usando Java com Quarkus.

## O que tem agora

- `GET /health`: verifica se a API esta rodando;
- `GET /menu`: mostra um cardapio fixo;
- `POST /orders`: cria um pedido;
- `GET /orders`: lista os pedidos criados.

Por enquanto nao tem frontend, DB2, Kafka/Event Streams nem microsservicos separados. A ideia e comecar pelo basico e evoluir aos poucos.

## Como rodar

```bash
cd backend/order-service
./mvnw quarkus:dev
```

Depois acesse:

- `http://localhost:8081/health`
- `http://localhost:8081/menu`
- `http://localhost:8081/orders`
- `http://localhost:8081/q/swagger-ui`

## Exemplo de criacao de pedido

```bash
curl -X POST http://localhost:8081/orders \
  -H "Content-Type: application/json" \
  -d '{"item":"Hamburguer","quantity":1}'
```
