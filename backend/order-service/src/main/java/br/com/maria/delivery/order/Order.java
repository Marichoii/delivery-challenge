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

    @Column(nullable = false, length = 36)
    public String customerId;

    @Column(nullable = false, length = 36)
    public String restaurantId;

    @Column(nullable = false, length = 36)
    public String itemId;

    @Column(nullable = false)
    public String itemName;

    @Column(nullable = false)
    public double price;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    public OrderStatus status;

    public Order() {
    }

    public Order(String id, String customerId, String restaurantId, String itemId, String itemName, double price, OrderStatus status) {
        this.id = id;
        this.customerId = customerId;
        this.restaurantId = restaurantId;
        this.itemId = itemId;
        this.itemName = itemName;
        this.price = price;
        this.status = status;
    }
}
