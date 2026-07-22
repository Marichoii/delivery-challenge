package br.com.maria.delivery.order;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.List;

@JsonPropertyOrder({"name", "items"})
public class MenuCategory {

    public String name;
    public List<MenuItem> items;

    public MenuCategory() {
    }
    public MenuCategory(String name, List<MenuItem> items) {
        this.name = name;
        this.items = items;
    }
}
