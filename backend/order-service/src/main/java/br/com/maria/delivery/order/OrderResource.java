package br.com.maria.delivery.order;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Path("/")
@Produces(MediaType.APPLICATION_JSON)
public class OrderResource {

    private final MenuService menuService;
    private final OrderService orderService;

    public OrderResource(MenuService menuService, OrderService orderService) {
        this.menuService = menuService;
        this.orderService = orderService;
    }
    
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
    public List<MenuCategory> menuCategories() {
        return menuService.listCategories();
    }

    @POST
    @Path("/orders")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response create(CreateOrderRequest request) {

        if (request == null
                || request.itemId == null
                || request.itemId.isBlank()
                || request.quantity <= 0) {

            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of(
                        "error",
                        "Item e quantidade válida são obrigatórios."
                    ))
                    .build();
        }

        Optional<Order> order = orderService.create(request);
        if (order.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(Map.of("error", "Item não encontrado no cardápio."))
                    .build();
        }

        return Response.status(Response.Status.CREATED)
                .entity(order.get())
                .build();
    }

    @GET
    @Path("/orders")
    public List<Order> list() {
        return orderService.list();
    }

    @GET
    @Path("/orders/{id}")
    public Response findById(@PathParam("id") String id) {
        Order order = orderService.findById(id);

        if (order == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(Map.of("error", "Pedido não encontrado."))
                    .build();
        }

        return Response.ok(order).build();
    }

    @GET
    @Path("/orders/{id}/history")
    public List<OrderHistory> history(@PathParam("id") String id) {
        return orderService.history(id);
    }

    @POST
    @Path("/orders/{id}/confirm")
    public Response confirm(@PathParam("id") String id) {
        return updateStatus(id, OrderStatus.CONFIRMED);
    }

    @POST
    @Path("/orders/{id}/prepare")
    public Response prepare(@PathParam("id") String id) {
        return updateStatus(id, OrderStatus.PREPARING);
    }

    @POST
    @Path("/orders/{id}/ready")
    public Response ready(@PathParam("id") String id) {
        return updateStatus(id, OrderStatus.READY);
    }

    @POST
    @Path("/orders/{id}/deliver")
    public Response deliver(@PathParam("id") String id) {
        return updateStatus(id, OrderStatus.DELIVERED);
    }

    private Response updateStatus(String id, OrderStatus status) {
        StatusUpdateResult result = orderService.updateStatus(id, status);
        if (result.notFound) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(Map.of("error", result.message))
                    .build();
        }

        if (result.invalidTransition) {
            return Response.status(Response.Status.CONFLICT)
                    .entity(Map.of("error", result.message))
                    .build();
        }

        return Response.ok(result.order).build();
    }
}
