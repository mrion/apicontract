package ch.msrion.apicontract.http;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.api.contract.RouterFactoryOptions;
import io.vertx.ext.web.api.contract.openapi3.OpenAPI3RouterFactory;
import io.vertx.ext.web.handler.BodyHandler;

/**
 * @author <a href="https://www.msrion.ch/">Marcial Rion</a>
 */
public class HttpServerVerticle extends AbstractVerticle {

  public static final String CONFIG_HTTP_SERVER_PORT = "http.server.port";

  private static final Logger LOGGER = LoggerFactory.getLogger(HttpServerVerticle.class);

  @Override
  public void start(Future<Void> startFuture) throws Exception {
    HttpServer server = vertx.createHttpServer();
    Router router = Router.router(vertx);

    router.route().handler(BodyHandler.create().setDeleteUploadedFilesOnEnd(false).setUploadsDirectory("file-uploads").setMergeFormAttributes(false));

    OpenAPI3RouterFactory.create(vertx, "src/main/resources/openapi.yml", ar -> {
      if (ar.succeeded()) {
        // Spec loaded with success
        OpenAPI3RouterFactory routerFactory = ar.result();
        RouterFactoryOptions options = new RouterFactoryOptions()
          .setMountNotImplementedHandler(true);
        routerFactory.setOptions(options);
        //routerFactory.addHandlerByOperationId("uploadJSON", this::uploadJSON);
        routerFactory.mountServicesFromExtensions();

        router.mountSubRouter("/api", routerFactory.getRouter());
        
        int portNumber = config().getInteger(CONFIG_HTTP_SERVER_PORT, 8080);

        LOGGER.info("Starting up HTTP server on port " + portNumber + "...");

        server.requestHandler(router).listen(portNumber, ar2 -> {
          if (ar2.succeeded()) {
            LOGGER.info("HTTP server running on port " + portNumber + ".");
            startFuture.complete();
          } else {
            LOGGER.error("Could not start a HTTP server", ar2.cause());
            startFuture.fail(ar2.cause());
          }
        });
      } else {
        // Something went wrong during router factory initialization
        Throwable exception = ar.cause();
      }
    });
  }
}
