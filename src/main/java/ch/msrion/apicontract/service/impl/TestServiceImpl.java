package ch.msrion.apicontract.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.msrion.apicontract.service.TestService;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.api.OperationRequest;
import io.vertx.ext.web.api.OperationResponse;


public class TestServiceImpl implements TestService {

  private static final Logger LOGGER = LoggerFactory.getLogger(TestServiceImpl.class);

  @Override
  public void uploadJSON(JsonObject body, OperationRequest context, Handler<AsyncResult<OperationResponse>> resultHandler) {
    LOGGER.info("Received JSON object...");
    if ( body == null) {
      LOGGER.info("Body is null");
    }
    else {
      LOGGER.info(body.toString());
    }
    resultHandler.handle(
      Future.succeededFuture(
        OperationResponse.completedWithPlainText(Buffer.buffer("Hello world!"))
      )
    );      
  }
}