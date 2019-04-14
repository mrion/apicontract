package ch.msrion.apicontract;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.msrion.apicontract.http.HttpServerVerticle;
import ch.msrion.apicontract.service.TestService;
import ch.msrion.apicontract.service.impl.TestServiceImpl;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.api.RequestParameter;
import io.vertx.ext.web.api.RequestParameters;
import io.vertx.serviceproxy.ServiceBinder;

public class MainVerticle extends AbstractVerticle {

  public static final String CONFIG_HTTP_SERVER_PORT = "http.server.port";

  private static final Logger LOGGER = LoggerFactory.getLogger(MainVerticle.class);

  @Override
  public void start(Future<Void> startFuture) throws Exception {
    LOGGER.info("Starting up application...");

    TestService service = new TestServiceImpl();
    final ServiceBinder serviceBinder = new ServiceBinder(vertx).setAddress("testService");
    MessageConsumer<JsonObject> serviceConsumer = serviceBinder.register(TestService.class, service);

    vertx.deployVerticle(HttpServerVerticle.class, new DeploymentOptions().setInstances(2), ar -> {
      if (ar.succeeded()) {
        LOGGER.info("HttpServerVerticle deployed sucessfully.");
        startFuture.complete();
      } else {
        LOGGER.error("HttpServerVerticle deployment failed!", ar.cause());
        startFuture.fail(ar.cause());
      }
    });
  }

  private void uploadJSON(RoutingContext context) {
    try {
      RequestParameters params = context.get("parsedParameters");
      RequestParameter body = params.body();
      body.getJsonObject().put("id", "1234567890");
      context.response().setStatusCode(HttpResponseStatus.CREATED.code());
      context.response().putHeader("Content-Type", "application/json");
      context.response().putHeader("Content-Encoding", "UTF-8");
      context.response().setChunked(true);
      context.response().write(body.toJson().toString());
    }
    catch (Exception e) {
      // do some error handling
    }
    finally {
      context.response().end();
    }
  }
  
  // Comodity to run it in VS Code
  public static void main(String[] args) {
    Vertx vertx = Vertx.vertx();
    vertx.deployVerticle(new MainVerticle());
  }
}
