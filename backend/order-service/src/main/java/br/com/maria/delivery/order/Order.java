package br.com.maria.delivery.order;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.UUID;

@Entity(name = "CustomerOrder")
@Table(name = "orders")
public class Order {

    @Id
    public UUID id;

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
    public String status;

    public Order() {
    }

    public Order(UUID id, String customerName, String itemId, String itemName, int quantity, double total, String status) {
        this.id = id;
        this.customerName = customerName;
        this.itemId = itemId;
        this.itemName = itemName;
        this.quantity = quantity;
        this.total = total;
        this.status = status;
    }
}
