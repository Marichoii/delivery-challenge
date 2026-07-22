package br.com.maria.delivery.order;

public class MenuItem {

    public String id;
    public String name;
    public double price;

    public MenuItem() {
    }

    public MenuItem(String id, String name, double price) {
        this.id = id;
        this.name = name;
        this.price = price;
    }
}
