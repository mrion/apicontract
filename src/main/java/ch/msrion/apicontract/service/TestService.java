package ch.msrion.apicontract.service;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.api.OperationRequest;
import io.vertx.ext.web.api.OperationResponse;
import io.vertx.ext.web.api.generator.WebApiServiceGen;

@WebApiServiceGen
public interface TestService {
  public void uploadJson(JsonObject body, OperationRequest context, Handler<AsyncResult<OperationResponse>> resultHandler);
  public void uploadPdf(JsonObject body, OperationRequest context, Handler<AsyncResult<OperationResponse>> resultHandler);
}