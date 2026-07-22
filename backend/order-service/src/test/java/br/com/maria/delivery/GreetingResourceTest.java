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
                .body("{\"item\":\"Hamburguer\",\"quantity\":1}")
                .when().post("/orders")
                .then()
                .statusCode(201)
                .body("item", is("Hamburguer"))
                .body("quantity", is(1))
                .body("status", is("CREATED"));
    }

}
