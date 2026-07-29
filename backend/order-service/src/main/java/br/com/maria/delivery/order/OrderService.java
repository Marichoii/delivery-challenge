package br.com.maria.delivery.order;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class OrderService {

    private static final String CUSTOMER_NAME = "Cliente MVP";

    private final EntityManager entityManager;
    private final MenuService menuService;

    public OrderService(EntityManager entityManager, MenuService menuService) {
        this.entityManager = entityManager;
        this.menuService = menuService;
    }

    @Transactional
    public Optional<Order> create(CreateOrderRequest request) {
        Optional<MenuItem> menuItem = menuService.findItemById(request.itemId);
        if (menuItem.isEmpty()) {
            return Optional.empty();
        }

        MenuItem item = menuItem.get();
        Order order = new Order(
                UUID.randomUUID().toString(),
                CUSTOMER_NAME,
                item.id,
                item.name,
                request.quantity,
                item.price * request.quantity,
                OrderStatus.CREATED
        );

        entityManager.persist(order);
        registerHistory(order.id, order.status, "Pedido criado.");
        return Optional.of(order);
    }

    public List<Order> list() {
        return entityManager
                .createQuery("from CustomerOrder order by itemName", Order.class)
                .getResultList();
    }

    public Order findById(String id) {
        return entityManager.find(Order.class, id);
    }

    public List<OrderHistory> history(String orderId) {
        return entityManager
                .createQuery("from OrderHistory where orderId = :orderId order by createdAt", OrderHistory.class)
                .setParameter("orderId", orderId)
                .getResultList();
    }

    @Transactional
    public StatusUpdateResult updateStatus(String id, OrderStatus nextStatus) {
        Order order = findById(id);
        if (order == null) {
            return StatusUpdateResult.notFound();
        }

        if (!order.status.canMoveTo(nextStatus)) {
            return StatusUpdateResult.invalidTransition(order.status, nextStatus);
        }

        order.status = nextStatus;
        registerHistory(order.id, nextStatus, descriptionFor(nextStatus));
        return StatusUpdateResult.updated(order);
    }

    private void registerHistory(String orderId, OrderStatus status, String description) {
        entityManager.persist(new OrderHistory(orderId, status, description));
    }

    private String descriptionFor(OrderStatus status) {
        return switch (status) {
            case CONFIRMED -> "Pedido confirmado pelo restaurante.";
            case PREPARING -> "Pedido em preparo.";
            case READY -> "Pedido pronto para entrega.";
            case DELIVERED -> "Pedido entregue ao cliente.";
            case CREATED -> "Pedido criado.";
        };
    }
}
