package br.com.maria.delivery.order;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;

import java.util.List;

@ApplicationScoped
public class CustomerService {

    private final EntityManager entityManager;

    public CustomerService(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public List<Customer> list() {
        return entityManager.createQuery("from Customer", Customer.class).getResultList();
    }
}
