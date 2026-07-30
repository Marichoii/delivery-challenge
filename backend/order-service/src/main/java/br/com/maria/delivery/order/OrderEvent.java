package br.com.maria.delivery.order;

import java.time.Instant;

public class OrderEvent {

    public String type;
    public String orderId;
    public OrderStatus status;
    public Instant occurredAt;

    public OrderEvent() {
    }

    public OrderEvent(String type, String orderId, OrderStatus status) {
        this.type = type;
        this.orderId = orderId;
        this.status = status;
        this.occurredAt = Instant.now();
    }
}
