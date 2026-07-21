package br.com.maria.delivery;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

@QuarkusTest
class GreetingResourceTest {
    @Test
    void testOrderStatusEndpoint() {
        given()
          .when().get("/orders/status")
          .then()
             .statusCode(200)
             .body("service", is("order-service"))
             .body("status", is("UP"));
    }

}