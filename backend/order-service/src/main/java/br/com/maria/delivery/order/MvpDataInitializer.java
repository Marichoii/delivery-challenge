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
        // Dados fixos do MVP: por enquanto o sistema tem 1 cliente e 1 restaurante.
        if (entityManager.find(Customer.class, OrderService.CUSTOMER_ID) == null) {
            entityManager.persist(new Customer(
                    OrderService.CUSTOMER_ID,
                    "Cliente MVP",
                    "Rua das Flores, 123"
            ));
        }

        if (entityManager.find(Restaurant.class, OrderService.RESTAURANT_ID) == null) {
            entityManager.persist(new Restaurant(
                    OrderService.RESTAURANT_ID,
                    "Restaurante MVP",
                    "Avenida Central, 456",
                    "Angus Divino",
                    120.0,
                    "Restaurante simples do MVP, usado para praticar pedidos, DB2 e eventos."
            ));
        }
    }
}
