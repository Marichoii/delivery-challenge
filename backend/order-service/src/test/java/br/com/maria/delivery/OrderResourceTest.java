package br.com.maria.delivery;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;

@QuarkusTest
class OrderResourceTest {

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
    void testListMvpRestaurant() {
        given()
                .when().get("/restaurants")
                .then()
                .statusCode(200)
                .body("[0].id", is("restaurante-mvp"))
                .body("[0].name", is("Restaurante MVP"));
    }

    @Test
    void testListMvpCustomer() {
        given()
                .when().get("/customers")
                .then()
                .statusCode(200)
                .body("[0].id", is("cliente-mvp"))
                .body("[0].name", is("Maria Edduarda"));
    }

    @Test
    void testListMenu() {
        given()
                .when().get("/menu")
                .then()
                .statusCode(200)
                .body("[0].id", notNullValue())
                .body("[0].name", notNullValue());
    }

    @Test
    void testCreateOrderMissingItemId() {
        given()
                .contentType("application/json")
                .body("{}")
                .when().post("/orders")
                .then()
                .statusCode(400)
                .body("error", is("itemId é obrigatório."));
    }

    @Test
    void testCreateOrder() {
        given()
                .contentType("application/json")
                .body("{\"itemId\":\"angus-divino\"}")
                .when().post("/orders")
                .then()
                .statusCode(201)
                .body("customerId", is("cliente-mvp"))
                .body("restaurantId", is("restaurante-mvp"))
                .body("itemName", is("Angus Divino"))
                .body("price", is(120.0F))
                .body("status", is("CREATED"));
    }

    @Test
    void testUpdateOrderStatus() {
        String id = given()
                .contentType("application/json")
                .body("{\"itemId\":\"angus-divino\"}")
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
    void testRejectInvalidStatusTransition() {
        String id = given()
                .contentType("application/json")
                .body("{\"itemId\":\"angus-divino\"}")
                .when().post("/orders")
                .then()
                .statusCode(201)
                .extract()
                .path("id");

        given()
                .when().post("/orders/" + id + "/deliver")
                .then()
                .statusCode(409)
                .body("error", is("Transição inválida de CREATED para DELIVERED."));
    }

    @Test
    void testOrderHistory() {
        String id = given()
                .contentType("application/json")
                .body("{\"itemId\":\"angus-divino\"}")
                .when().post("/orders")
                .then()
                .statusCode(201)
                .extract()
                .path("id");

        given()
                .when().post("/orders/" + id + "/confirm")
                .then()
                .statusCode(200);

        given()
                .when().get("/orders/" + id + "/history")
                .then()
                .statusCode(200)
                .body("[0].status", is("CREATED"))
                .body("[1].status", is("CONFIRMED"));
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
