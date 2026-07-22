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

@Path("/")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class OrderResource {

    private final List<MenuItem> menu = List.of(
            new MenuItem("Carpaccio", 95.00),
            new MenuItem("Bolinhas de Carbonara", 50.00),
            new MenuItem("Crosta de Pães Especiais", 120.00),
            new MenuItem("Angus Divino", 150.00),
            new MenuItem("Refrigerantes", 8.00),
            new MenuItem("Sucos Naturais", 12.00),
            new MenuItem("Torta de Café", 20.00),
            new MenuItem("Bolo Quente", 49.00)
    );

    private final List<Order> orders = new ArrayList<>();

    @GET
    @Path("/health")
    public Map<String, String> status() {
        return Map.of(
            "service", "delivery-backend",
            "status", "UP"
        );
    }

    @GET
    @Path("/menu")
    public List<MenuItem> menu() {
        return menu;
    }

    @POST
    @Path("/orders")
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
    @Path("/orders")
    public List<Order> list() {
        return orders;
    }
}
