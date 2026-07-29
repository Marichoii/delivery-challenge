package br.com.maria.delivery.order;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity(name = "OrderHistory")
@Table(name = "order_history")
public class OrderHistory {

    @Id
    @Column(length = 36)
    public String id;

    @Column(nullable = false, length = 36)
    public String orderId;

    @Column(nullable = false)
    public String status;

    @Column(nullable = false)
    public String description;

    @Column(nullable = false)
    public Instant createdAt;

    public OrderHistory() {
    }

    public OrderHistory(String orderId, String status, String description) {
        this.id = UUID.randomUUID().toString();
        this.orderId = orderId;
        this.status = status;
        this.description = description;
        this.createdAt = Instant.now();
    }
}
