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

@Path("/")
@Produces(MediaType.APPLICATION_JSON)
public class OrderResource {

    private final OrderService orderService;
    private final CustomerService customerService;
    private final RestaurantService restaurantService;
    private final MenuItemService menuItemService;

    public OrderResource(OrderService orderService, CustomerService customerService, RestaurantService restaurantService, MenuItemService menuItemService) {
        this.orderService = orderService;
        this.customerService = customerService;
        this.restaurantService = restaurantService;
        this.menuItemService = menuItemService;
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
    @Path("/customers")
    public List<Customer> customers() {
        return customerService.list();
    }

    @GET
    @Path("/menu")
    public List<MenuItem> menu() {
        return menuItemService.listByRestaurant(OrderService.RESTAURANT_ID);
    }

    @GET
    @Path("/restaurants")
    public List<Restaurant> restaurants() {
        return restaurantService.list();
    }

    @POST
    @Path("/orders")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response create(CreateOrderRequest request) {
        Order order = orderService.create(request);
        return Response.status(Response.Status.CREATED).entity(order).build();
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
