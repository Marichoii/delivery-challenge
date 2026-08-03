package br.com.maria.delivery.order;

import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class MvpDataInitializer {

    private final EntityManager entityManager;

    public MvpDataInitializer(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Transactional
    void onStart(@Observes StartupEvent event) {
        syncCustomer();
        syncRestaurant();
        syncMenuItems();
    }

    private void syncCustomer() {
        Customer customer = entityManager.find(Customer.class, OrderService.CUSTOMER_ID);
        if (customer == null) {
            entityManager.persist(new Customer(
                    OrderService.CUSTOMER_ID,
                    "Maria Edduarda",
                    "SQN 312 Bloco C"
            ));
        } else {
            customer.name = "Maria Edduarda";
            customer.address = "SQN 312 Bloco C";
        }
    }

    private void syncRestaurant() {
        Restaurant restaurant = entityManager.find(Restaurant.class, OrderService.RESTAURANT_ID);
        if (restaurant == null) {
            entityManager.persist(new Restaurant(
                    OrderService.RESTAURANT_ID,
                    "Restaurante MVP",
                    "SHCN/CL Qd 413 Bloco A",
                    "Restaurante simples do MVP, usado para praticar pedidos, DB2 e eventos."
            ));
        } else {
            restaurant.name = "Restaurante MVP";
            restaurant.address = "SHCN/CL Qd 413 Bloco A";
            restaurant.description = "Restaurante simples do MVP, usado para praticar pedidos, DB2 e eventos.";
        }
    }

    private void syncMenuItems() {
        upsertItem("carpaccio-salmao",  "Carpaccio de Salmão",        50.0);
        upsertItem("dadinho-tapioca",   "Dadinho de Tapioca",          30.0);
        upsertItem("gelo",              "Gelo",                         5.0);
        upsertItem("crostas-paes",      "Crostas de Pães Especiais",  110.0);
        upsertItem("risoto-camarao",    "Risoto de Camarão",           70.0);
        upsertItem("angus-divino",      "Angus Divino",               120.0);
        upsertItem("torta-cafe",        "Torta de Café",               20.0);
        upsertItem("bolo-quente",       "Bolo Quente",                 25.0);
        upsertItem("quindim",           "Quindim",                     20.0);
        upsertItem("cerveja",           "Cerveja",                     10.0);
        upsertItem("vinho",             "Vinho",                       30.0);
        upsertItem("refrigerante",      "Refrigerante",                 9.0);
        upsertItem("suco",              "Suco",                        12.0);
        upsertItem("agua",              "Água",                         5.0);
    }

    private void upsertItem(String id, String name, double price) {
        MenuItem item = entityManager.find(MenuItem.class, id);
        if (item == null) {
            entityManager.persist(new MenuItem(id, OrderService.RESTAURANT_ID, name, price));
        } else {
            item.name = name;
            item.price = price;
        }
    }
}
