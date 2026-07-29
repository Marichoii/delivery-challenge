package br.com.maria.delivery.order;

public interface OrderEventPublisher {

    void publish(OrderEvent event);
}
