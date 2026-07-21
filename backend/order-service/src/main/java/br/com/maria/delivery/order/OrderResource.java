package br.com.maria.delivery.order;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Path("/orders")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class OrderResource {

    private final List<Order> orders = new ArrayList<>();

    @GET
    @Path("/status")
    public Map<String, String> status() {
        return Map.of(
            "service", "order-service",
            "status", "UP"
        );
    }

    @POST
    public Response create(CreateOrderRequest request) {

        if (request == null
                || request.item == null
                || request.item.isBlank()
                || request.quantity <= 0) {

            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of(
                        "error",
                        "Item e quantidade válida são obrigatórios."
                    ))
                    .build();
        }

        Order order = new Order(
            UUID.randomUUID(),
            request.item,
            request.quantity,
            "CREATED"
        );

        orders.add(order);

        return Response.status(Response.Status.CREATED)
                .entity(order)
                .build();
    }

    @GET
    public List<Order> list() {
        return orders;
    }
}
