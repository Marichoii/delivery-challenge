package br.com.maria.delivery.order;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Path("/")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class OrderResource {

    private static final String CUSTOMER_NAME = "Cliente MVP";

    private final List<MenuCategory> menuCategories;
    private final EntityManager entityManager;

    public OrderResource(EntityManager entityManager) {
        this.entityManager = entityManager;
        this.menuCategories = new ArrayList<>();
        menuCategories.add(new MenuCategory("Entradinhas", List.of(
            new MenuItem("carpaccio-salmao", "Carpaccio de Salmão", 50.0),
            new MenuItem("dadinho-tapioca", "Dadinho de Tapioca", 30.0),
            new MenuItem("gelo", "Gelo", 5.0)
        )));
        menuCategories.add(new MenuCategory("Pratos Principais", List.of(
            new MenuItem("crostas-paes", "Crostas de Pães Especiais", 110.0),
            new MenuItem("risoto-camarao", "Risoto de Camarão", 70.0),
            new MenuItem("angus-divino", "Angus Divino", 120.0)
        )));
        menuCategories.add(new MenuCategory("Sobremesas", List.of(
            new MenuItem("torta-cafe", "Torta de Café", 20.0),
            new MenuItem("bolo-quente", "Bolo Quente", 25.0),
            new MenuItem("quindim", "Quindim", 20.0)
        )));
        menuCategories.add(new MenuCategory("Bebidas", List.of(
            new MenuItem("cerveja", "Cerveja", 10.0),
            new MenuItem("vinho", "Vinho", 30.0),
            new MenuItem("refrigerante", "Refrigerante", 9.0),
            new MenuItem("suco", "Suco", 12.0),
            new MenuItem("agua", "Água", 5.0)
        )));
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
        return menuCategories;
    }

    @POST
    @Path("/orders")
    @Transactional
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

        Optional<MenuItem> menuItem = menuCategories.stream()
                .flatMap(category -> category.items.stream())
                .filter(item -> item.id.equals(request.itemId))
                .findFirst();

        if (menuItem.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(Map.of("error", "Item não encontrado no cardápio."))
                    .build();
        }

        MenuItem item = menuItem.get();
        Order order = new Order(
            UUID.randomUUID(),
            CUSTOMER_NAME,
            item.id,
            item.name,
            request.quantity,
            item.price * request.quantity,
            "CREATED"
        );

        entityManager.persist(order);

        return Response.status(Response.Status.CREATED)
                .entity(order)
                .build();
    }

    @GET
    @Path("/orders")
    public List<Order> list() {
        return entityManager
                .createQuery("from CustomerOrder order by itemName", Order.class)
                .getResultList();
    }
}
