package br.com.maria.delivery.order;

public class MenuItem {

    public String category;
    public String name;
    public double price;

    public MenuItem() {
    }

    public MenuItem(String category, String name, double price) {
        this.category = category;
        this.name = name;
        this.price = price;
    }
}
