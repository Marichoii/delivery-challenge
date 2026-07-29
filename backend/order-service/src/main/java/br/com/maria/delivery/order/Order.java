package br.com.maria.delivery.order;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity(name = "CustomerOrder")
@Table(name = "orders")
public class Order {

    @Id
    @Column(length = 36)
    public String id;

    @Column(nullable = false)
    public String customerName;

    @Column(nullable = false)
    public String itemId;

    @Column(nullable = false)
    public String itemName;

    @Column(nullable = false)
    public int quantity;

    @Column(nullable = false)
    public double total;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    public OrderStatus status;

    public Order() {
    }

    public Order(String id, String customerName, String itemId, String itemName, int quantity, double total, OrderStatus status) {
        this.id = id;
        this.customerName = customerName;
        this.itemId = itemId;
        this.itemName = itemName;
        this.quantity = quantity;
        this.total = total;
        this.status = status;
    }
}
