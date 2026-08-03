package br.com.maria.delivery.order;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;

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
}
