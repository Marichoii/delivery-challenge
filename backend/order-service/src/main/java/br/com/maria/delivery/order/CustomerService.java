package br.com.maria.delivery.order;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

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

    public Customer findById(String id) {
        return entityManager.find(Customer.class, id);
    }

    @Transactional
    public Customer create(String name, String address) {
        Customer customer = new Customer(java.util.UUID.randomUUID().toString(), name, address);
        entityManager.persist(customer);
        return customer;
    }
}
