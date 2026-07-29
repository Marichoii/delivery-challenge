# Delivery Challenge

Inicio bem simples do backend de uma aplicacao de delivery usando Java com Quarkus.

## O que tem agora

- `GET /health`: verifica se a API esta rodando;
- `GET /menu`: mostra um cardapio fixo agrupado por categoria;
- `POST /orders`: cria um pedido usando o `itemId`, valida se o item existe e calcula o total;
- `GET /orders`: lista os pedidos criados;
- `GET /orders/{id}`: busca um pedido pelo id;
- `POST /orders/{id}/confirm`: muda o status para `CONFIRMED`;
- `POST /orders/{id}/prepare`: muda o status para `PREPARING`;
- `POST /orders/{id}/ready`: muda o status para `READY`;
- `POST /orders/{id}/deliver`: muda o status para `DELIVERED`;
- `GET /orders/{id}/history`: lista o historico de status do pedido.

Os pedidos ja sao persistidos via JPA/Hibernate. Em execucao normal, o backend aponta para DB2. Nos testes, ele usa H2 em memoria para nao depender do banco real.

Por enquanto nao tem Kafka/Event Streams nem microsservicos separados. A ideia e comecar pelo basico e evoluir aos poucos.

## Como rodar com DB2

Antes de subir o Quarkus, configure as variaveis do DB2.

Voce pode criar um `.env` local a partir do exemplo:

```bash
cd backend/order-service
cp .env.example .env
```

Depois edite o `.env` com seus dados reais. Esse arquivo nao deve ir para o Git.

Para carregar as variaveis no terminal:

```bash
set -a
source .env
set +a
```

Para DB2 local, normalmente fica assim:

```bash
export DB2_USERNAME=db2inst1
export DB2_PASSWORD=sua_senha
export DB2_JDBC_URL='jdbc:db2://localhost:50000/DELIVERY'
```

Para IBM Db2 Cloud, a URL costuma precisar de SSL no final:

```bash
export DB2_USERNAME=seu_usuario
export DB2_PASSWORD=sua_senha
export DB2_JDBC_URL='jdbc:db2://host-do-db2:porta/bludb:sslConnection=true;'
```

Nao coloque `user` nem `password` dentro da `DB2_JDBC_URL`, porque o Quarkus ja envia esses valores por `DB2_USERNAME` e `DB2_PASSWORD`.

Correto:

```bash
export DB2_USERNAME='seu_usuario'
export DB2_PASSWORD='sua_senha'
export DB2_JDBC_URL='jdbc:db2://host-do-db2:porta/bludb:sslConnection=true;'
```

Errado:

```bash
export DB2_JDBC_URL='jdbc:db2://host-do-db2:porta/bludb:user=seu_usuario;password=sua_senha;sslConnection=true;'
```

Se aparecer `ERRORCODE=-4499, SQLSTATE=58009`, confira principalmente se o host, porta, nome do banco e `sslConnection=true` correspondem exatamente aos dados de conexao do seu DB2.

Se aparecer `ERRORCODE=-4461, SQLSTATE=42815` com mensagem sobre `user`, remova `user` e `password` da URL e deixe esses valores apenas nas variaveis `DB2_USERNAME` e `DB2_PASSWORD`.

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

Depois acesse:

- `http://localhost:4200`

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

## Exemplo de mudanca de status

Depois de criar um pedido, copie o `id` retornado e use nas chamadas:

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

No IBM Db2 on Cloud, cada usuario tem seu proprio schema. O schema padrao do usuario nao e o mesmo nome do banco — e um codigo gerado pela IBM (por exemplo, `59E4EFB7`). Para confirmar os dados salvos pelo Quarkus, use o schema correto no Run SQL do console:

```sql
SELECT * FROM "59E4EFB7".ORDERS
```

### Tipo da coluna ID

O DB2 nao aceita o tipo `UUID` do Java diretamente — ele converte para `CHAR(16) FOR BIT DATA`, o que causa `ERRORCODE=-4474` na hora de salvar. A solucao foi mapear o campo `id` como `String` com `@Column(length = 36)` na entidade e gerar o UUID com `UUID.randomUUID().toString()` no codigo.

### Recriando a tabela com tipos corretos

Se a tabela foi criada com o tipo errado (por exemplo, antes da correcao do `UUID`), o Hibernate com `strategy=update` nao consegue alterar a coluna `ID` porque ela e chave primaria. Para recriar do zero, mude temporariamente para:

```properties
quarkus.hibernate-orm.schema-management.strategy=drop-and-create
```

Suba o Quarkus uma vez, confirme que a tabela foi recriada, depois volte para `update`.

### Variaveis de ambiente obrigatorias

O `application.properties` usa `${VARIAVEL}` sem fallback. Antes de rodar, exporte no terminal:

```bash
export DB2_USERNAME=seu_usuario
export DB2_PASSWORD=sua_senha
export DB2_JDBC_URL='jdbc:db2://host:porta/bludb:sslConnection=true;'
```

As variaveis precisam estar no mesmo terminal onde o `./mvnw quarkus:dev` sera executado. Para persistir entre sessoes, adicione ao seu `.zshrc` ou use um arquivo `.env` com `source`.
