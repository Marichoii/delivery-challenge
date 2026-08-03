package br.com.maria.delivery.order;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

import java.util.List;

@ApplicationScoped
public class RestaurantService {

    private final EntityManager entityManager;

    public RestaurantService(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public List<Restaurant> list() {
        return entityManager.createQuery("from Restaurant", Restaurant.class).getResultList();
    }

    public Restaurant findById(String id) {
        return entityManager.find(Restaurant.class, id);
    }

    @Transactional
    public Restaurant create(String name, String address, String description) {
        Restaurant restaurant = new Restaurant(java.util.UUID.randomUUID().toString(), name, address, description);
        entityManager.persist(restaurant);
        return restaurant;
    }
}
