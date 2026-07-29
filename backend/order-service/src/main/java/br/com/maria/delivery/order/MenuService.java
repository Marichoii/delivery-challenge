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
            new MenuItem("carpaccio-salmao", "Carpaccio de Salmão", 50.0),
            new MenuItem("dadinho-tapioca", "Dadinho de Tapioca", 30.0),
            new MenuItem("gelo", "Gelo", 5.0)
        )));
        menuCategories.add(new MenuCategory("Pratos Principais", List.of(
            new MenuItem("crostas-paes", "Crostas de Pães Especiais", 110.0),
            new MenuItem("risoto-camarao", "Risoto de Camarão", 70.0),
            new MenuItem("angus-divino", "Angus Divino", 120.0)
        )));
        menuCategories.add(new MenuCategory("Sobremesas", List.of(
            new MenuItem("torta-cafe", "Torta de Café", 20.0),
            new MenuItem("bolo-quente", "Bolo Quente", 25.0),
            new MenuItem("quindim", "Quindim", 20.0)
        )));
        menuCategories.add(new MenuCategory("Bebidas", List.of(
            new MenuItem("cerveja", "Cerveja", 10.0),
            new MenuItem("vinho", "Vinho", 30.0),
            new MenuItem("refrigerante", "Refrigerante", 9.0),
            new MenuItem("suco", "Suco", 12.0),
            new MenuItem("agua", "Água", 5.0)
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
