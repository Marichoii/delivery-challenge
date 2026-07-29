# Delivery Challenge

Aplicação de delivery inspirada no "iFood", criada como desafio para praticar backend, APIs REST, banco de dados, frontend e evolução gradual para microsservicos e event streaming!

**O objetivo não é construir uma plataforma completa**. O escopo é para ser reduzido apenas um fluxo simples com um cliente, um restaurante e um entregador, permitindo focar na arquitetura e na integração entre as partes.

## Escopo Atual

Nesta versão, o projeto possui:

- Backend em Java com Quarkus;
- Frontend simples em Angular;
- Persistencia de pedidos no IBM DB2;
- Cardápio fixo agrupado por categorias;
- Criação de pedidos;
- Mudança de status do pedido;
- Historico de status do pedido;
- Testes automatizados usando H2 em memória.

Ainda não foram implementados Kafka, IBM Event Streams ou microsserviços separados. Eles fazem parte da evolução planejada do desafio.
