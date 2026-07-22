package br.com.maria.delivery.order;

import java.util.UUID;

public class Order {

    public UUID id;
    public String customerName;
    public String itemId;
    public String itemName;
    public int quantity;
    public double total;
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
