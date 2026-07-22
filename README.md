# Delivery Challenge

Inicio bem simples do backend de uma aplicacao de delivery usando Java com Quarkus.

## O que tem agora

- `GET /health`: verifica se a API esta rodando;
- `GET /menu`: mostra um cardapio fixo agrupado por categoria;
- `POST /orders`: cria um pedido usando o `itemId`, valida se o item existe e calcula o total;
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

## Exemplo do cardapio

O endpoint `GET /menu` retorna primeiro a categoria e depois os itens:

```json
[
  {
    "name": "Entradinhas",
    "items": [
      {
        "id": "carpaccio-salmao",
        "name": "Carpaccio de Salmão",
        "price": 50.0
      }
    ]
  }
]
```

## Exemplo de criacao de pedido

```bash
curl -X POST http://localhost:8081/orders \
  -H "Content-Type: application/json" \
  -d '{"itemId":"angus-divino","quantity":2}'
```

O backend vai responder com o nome do item, quantidade, total e status `CREATED`.
