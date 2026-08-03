package br.com.maria.delivery.order;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "menu_items")
public class MenuItem {

    @Id
    @Column(length = 36)
    public String id;

    @Column(nullable = false, length = 36)
    public String restaurantId;

    @Column(nullable = false)
    public String name;

    @Column(nullable = false)
    public double price;

    public MenuItem() {
    }

    public MenuItem(String id, String restaurantId, String name, double price) {
        this.id = id;
        this.restaurantId = restaurantId;
        this.name = name;
        this.price = price;
    }
}
