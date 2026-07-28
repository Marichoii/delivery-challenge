package br.com.maria.delivery;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

@QuarkusTest
class GreetingResourceTest {
    @Test
    void testHealthEndpoint() {
        given()
          .when().get("/health")
          .then()
             .statusCode(200)
             .body("service", is("delivery-backend"))
             .body("status", is("UP"));
    }

    @Test
    void testMenuGroupedByCategory() {
        given()
                .when().get("/menu")
                .then()
                .statusCode(200)
                .body("[0].name", is("Entradinhas"))
                .body("[0].items[0].id", is("carpaccio-salmao"))
                .body("[0].items[0].name", is("Carpaccio de Salmão"))
                .body("[0].items[0].price", is(50.0F));
    }

    @Test
    void testCreateOrder() {
        given()
                .contentType("application/json")
                .body("{\"itemId\":\"angus-divino\",\"quantity\":2}")
                .when().post("/orders")
                .then()
                .statusCode(201)
                .body("itemId", is("angus-divino"))
                .body("itemName", is("Angus Divino"))
                .body("quantity", is(2))
                .body("total", is(240.0F))
                .body("status", is("CREATED"));
    }

    @Test
    void testCreateOrderWithInvalidItem() {
        given()
                .contentType("application/json")
                .body("{\"itemId\":\"item-inexistente\",\"quantity\":1}")
                .when().post("/orders")
                .then()
                .statusCode(404)
                .body("error", is("Item não encontrado no cardápio."));
    }

    @Test
    void testUpdateOrderStatus() {
        String id = given()
                .contentType("application/json")
                .body("{\"itemId\":\"suco\",\"quantity\":1}")
                .when().post("/orders")
                .then()
                .statusCode(201)
                .extract()
                .path("id");

        given()
                .when().post("/orders/" + id + "/confirm")
                .then()
                .statusCode(200)
                .body("status", is("CONFIRMED"));

        given()
                .when().get("/orders/" + id)
                .then()
                .statusCode(200)
                .body("status", is("CONFIRMED"));
    }

    @Test
    void testUpdateMissingOrderStatus() {
        given()
                .when().post("/orders/pedido-inexistente/confirm")
                .then()
                .statusCode(404)
                .body("error", is("Pedido não encontrado."));
    }

}
