package br.com.maria.delivery.order;

import java.util.UUID;

public class Order {

    public UUID id;
    public String item;
    public int quantity;
    public String status;

    public Order() {
    }

    public Order(UUID id, String item, int quantity, String status) {
        this.id = id;
        this.item = item;
        this.quantity = quantity;
        this.status = status;
    }
}
