package dev.rhcts.marketdata;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

@QuarkusTest
public class MarketDataSubscriptionResourceTest {

    @Test
    public void testHelloEndpoint() {
        given()
          .when().get("/subscriptions")
          .then()
             .statusCode(200)
             .body(is("Hello from RESTEasy Reactive"));
    }

}