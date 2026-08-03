package br.com.maria.delivery.order;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "restaurants")
public class Restaurant {

    @Id
    @Column(length = 36)
    public String id;

    @Column(nullable = false)
    public String name;

    @Column(nullable = false)
    public String address;

    @Column(length = 1000)
    public String description;

    public Restaurant() {
    }

    public Restaurant(String id, String name, String address, String description) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.description = description;
    }
}
