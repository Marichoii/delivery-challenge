package br.com.maria.delivery.order;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;

import java.util.List;

@ApplicationScoped
public class MenuItemService {

    private final EntityManager entityManager;

    public MenuItemService(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public List<MenuItem> listByRestaurant(String restaurantId) {
        return entityManager
                .createQuery("from MenuItem where restaurantId = :rid order by name", MenuItem.class)
                .setParameter("rid", restaurantId)
                .getResultList();
    }

    public MenuItem findById(String id) {
        return entityManager.find(MenuItem.class, id);
    }
}
