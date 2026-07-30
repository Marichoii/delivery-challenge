package br.com.maria.delivery.order;

import io.quarkus.arc.profile.UnlessBuildProfile;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.jboss.logging.Logger;

@ApplicationScoped
@UnlessBuildProfile("test")
public class OrderEventConsumer {

    private static final Logger LOG = Logger.getLogger(OrderEventConsumer.class);

    @Incoming("order-events-in")
    public void consume(String event) {
        LOG.infof("Evento de pedido recebido: %s", event);
    }
}
