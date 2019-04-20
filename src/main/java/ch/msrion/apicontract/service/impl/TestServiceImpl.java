package ch.msrion.apicontract.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.msrion.apicontract.service.TestService;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.api.OperationRequest;
import io.vertx.ext.web.api.OperationResponse;


public class TestServiceImpl implements TestService {

  private static final Logger LOGGER = LoggerFactory.getLogger(TestServiceImpl.class);

  @Override
  public void uploadJson(JsonObject body, OperationRequest context, Handler<AsyncResult<OperationResponse>> resultHandler) {
    LOGGER.info("Received JSON object...");
    if ( body == null) {
      LOGGER.info("Body is null");
    }
    else {
      LOGGER.info(body.toString());
      body.put("id", "1234567890");
    }
    resultHandler.handle(
      Future.succeededFuture(
        new OperationResponse().setPayload(body.toBuffer()).setStatusCode(HttpResponseStatus.CREATED.code())
      )
    );      
  }
}