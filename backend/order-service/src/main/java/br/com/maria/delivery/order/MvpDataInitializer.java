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
        Customer customer = entityManager.find(Customer.class, OrderService.CUSTOMER_ID);
        if (customer == null) {
            entityManager.persist(new Customer(
                    OrderService.CUSTOMER_ID,
                    "Cliente MVP",
                    "SQN 312 Bloco C"
            ));
        } else {
            customer.name = "Cliente MVP";
            customer.address = "SQN 312 Bloco C";
        }

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
}
