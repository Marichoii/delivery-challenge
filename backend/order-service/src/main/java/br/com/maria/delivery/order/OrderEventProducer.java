package br.com.maria.delivery.order;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.enterprise.context.ApplicationScoped;
import io.quarkus.arc.profile.UnlessBuildProfile;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;

@ApplicationScoped
@UnlessBuildProfile("test")
public class OrderEventProducer implements OrderEventPublisher {

    private final Emitter<String> emitter;
    private final ObjectMapper objectMapper;

    public OrderEventProducer(@Channel("order-events") Emitter<String> emitter, ObjectMapper objectMapper) {
        this.emitter = emitter;
        this.objectMapper = objectMapper;
    }

    @Override
    public void publish(OrderEvent event) {
        try {
            emitter.send(objectMapper.writeValueAsString(event));
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("Não foi possível serializar o evento do pedido.", exception);
        }
    }
}
