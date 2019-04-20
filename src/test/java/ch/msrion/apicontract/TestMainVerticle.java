package ch.msrion.apicontract;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.reactivex.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.junit5.Timeout;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import io.vertx.reactivex.core.buffer.Buffer;
import io.vertx.reactivex.ext.web.client.HttpResponse;
import io.vertx.reactivex.ext.web.client.WebClient;

@ExtendWith(VertxExtension.class)
public class TestMainVerticle {

  @BeforeEach
  void deploy_verticle(Vertx vertx, VertxTestContext testContext) {
    vertx.deployVerticle(new MainVerticle(), testContext.succeeding(id -> testContext.completeNow()));
  }

  @Test
  @DisplayName("Should start a Web Server on port 8080")
  @Timeout(value = 10, timeUnit = TimeUnit.SECONDS)
  void start_http_server(Vertx vertx, VertxTestContext testContext) throws Throwable {
    vertx.createHttpClient().getNow(8080, "localhost", "/", response -> testContext.verify(() -> {
      assertTrue(response.statusCode() == HttpResponseStatus.NOT_FOUND.code());
      response.handler(body -> {
        assertTrue(body.toString().contains("Resource not found"));
        testContext.completeNow();
      });
    }));
  }

  @Test
  @DisplayName("Create JSON object")
  @Timeout(value = 10, timeUnit = TimeUnit.SECONDS)
  void create_json_object(Vertx vertx, VertxTestContext testContext) throws Throwable {
    WebClient client = WebClient.create(vertx);

    client.post(8080, "localhost", "/api/json")
    .sendJsonObject(new JsonObject()
      .put("from", "test@test.com")
      .put("to", "test@example.com"), ar -> {
      if (ar.succeeded()) {
        HttpResponse<Buffer> response = ar.result();
        assertTrue(response.statusCode() == HttpResponseStatus.CREATED.code());
        assertTrue(response.body().toString().contains("id"));
        testContext.completeNow();
      }
      else {
        testContext.failNow(ar.cause());
      }
    });
  }

}
