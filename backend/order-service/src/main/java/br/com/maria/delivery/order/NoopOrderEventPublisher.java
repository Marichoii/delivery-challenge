package br.com.maria.delivery.order;

import jakarta.enterprise.context.ApplicationScoped;
import io.quarkus.arc.profile.IfBuildProfile;

@ApplicationScoped
@IfBuildProfile("test")
public class NoopOrderEventPublisher implements OrderEventPublisher {

    @Override
    public void publish(OrderEvent event) {
        // Tests do not need a running Kafka broker.
    }
}
