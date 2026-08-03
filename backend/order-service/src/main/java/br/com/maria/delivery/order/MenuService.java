package br.com.maria.delivery.order;

import jakarta.enterprise.context.ApplicationScoped;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class MenuService {

    private final List<MenuCategory> menuCategories;

    public MenuService() {
        this.menuCategories = new ArrayList<>();
        menuCategories.add(new MenuCategory("Entradinhas", List.of(
            new MenuItem("carpaccio-salmao", OrderService.RESTAURANT_ID, "Carpaccio de Salmão", 50.0),
            new MenuItem("dadinho-tapioca", OrderService.RESTAURANT_ID, "Dadinho de Tapioca", 30.0),
            new MenuItem("gelo", OrderService.RESTAURANT_ID, "Gelo", 5.0)
        )));
        menuCategories.add(new MenuCategory("Pratos Principais", List.of(
            new MenuItem("crostas-paes", OrderService.RESTAURANT_ID, "Crostas de Pães Especiais", 110.0),
            new MenuItem("risoto-camarao", OrderService.RESTAURANT_ID, "Risoto de Camarão", 70.0),
            new MenuItem("angus-divino", OrderService.RESTAURANT_ID, "Angus Divino", 120.0)
        )));
        menuCategories.add(new MenuCategory("Sobremesas", List.of(
            new MenuItem("torta-cafe", OrderService.RESTAURANT_ID, "Torta de Café", 20.0),
            new MenuItem("bolo-quente", OrderService.RESTAURANT_ID, "Bolo Quente", 25.0),
            new MenuItem("quindim", OrderService.RESTAURANT_ID, "Quindim", 20.0)
        )));
        menuCategories.add(new MenuCategory("Bebidas", List.of(
            new MenuItem("cerveja", OrderService.RESTAURANT_ID, "Cerveja", 10.0),
            new MenuItem("vinho", OrderService.RESTAURANT_ID, "Vinho", 30.0),
            new MenuItem("refrigerante", OrderService.RESTAURANT_ID, "Refrigerante", 9.0),
            new MenuItem("suco", OrderService.RESTAURANT_ID, "Suco", 12.0),
            new MenuItem("agua", OrderService.RESTAURANT_ID, "Água", 5.0)
        )));
    }

    public List<MenuCategory> listCategories() {
        return menuCategories;
    }

    public Optional<MenuItem> findItemById(String itemId) {
        return menuCategories.stream()
                .flatMap(category -> category.items.stream())
                .filter(item -> item.id.equals(itemId))
                .findFirst();
    }
}
