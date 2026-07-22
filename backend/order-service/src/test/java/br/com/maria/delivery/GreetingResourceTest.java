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
                .body("total", is(300.0F))
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

}
