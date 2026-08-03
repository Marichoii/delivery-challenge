package br.com.maria.delivery.order;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class OrderService {

    // IDs fixos — só existe 1 cliente e 1 restaurante no MVP
    public static final String CUSTOMER_ID = "cliente-mvp";
    public static final String RESTAURANT_ID = "restaurante-mvp";

    private final EntityManager entityManager;
    private final MenuItemService menuItemService;
    private final OrderEventPublisher eventPublisher;

    public OrderService(EntityManager entityManager, MenuItemService menuItemService, OrderEventPublisher eventPublisher) {
        this.entityManager = entityManager;
        this.menuItemService = menuItemService;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public Order create(CreateOrderRequest request) {
        MenuItem item = menuItemService.findById(request.itemId);
        if (item == null) {
            throw new IllegalArgumentException("Item não encontrado: " + request.itemId);
        }

        Order order = new Order(
                UUID.randomUUID().toString(),
                CUSTOMER_ID,
                RESTAURANT_ID,
                item.id,
                item.name,
                item.price,
                OrderStatus.CREATED
        );

        entityManager.persist(order);
        registerHistory(order.id, order.status, "Pedido criado.");
        publishEvent(order.id, order.status);
        return order;
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
        publishEvent(order.id, nextStatus);
        return StatusUpdateResult.updated(order);
    }

    private void registerHistory(String orderId, OrderStatus status, String description) {
        entityManager.persist(new OrderHistory(orderId, status, description));
    }

    private void publishEvent(String orderId, OrderStatus status) {
        eventPublisher.publish(new OrderEvent(eventTypeFor(status), orderId, status));
    }

    private String eventTypeFor(OrderStatus status) {
        return switch (status) {
            case CREATED   -> "ORDER_CREATED";
            case CONFIRMED -> "ORDER_CONFIRMED";
            case PREPARING -> "ORDER_PREPARING";
            case READY     -> "ORDER_READY";
            case DELIVERED -> "ORDER_DELIVERED";
        };
    }

    private String descriptionFor(OrderStatus status) {
        return switch (status) {
            case CREATED   -> "Pedido criado.";
            case CONFIRMED -> "Pedido confirmado pelo restaurante.";
            case PREPARING -> "Pedido em preparo.";
            case READY     -> "Pedido pronto para entrega.";
            case DELIVERED -> "Pedido entregue ao cliente.";
        };
    }
}
