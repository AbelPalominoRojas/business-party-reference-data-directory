package com.ironman.partyreference.expose.exception;

import static com.ironman.partyreference.application.exception.ExceptionConstants.ERROR_TYPE_TECHNICAL;
import static com.ironman.partyreference.application.exception.ExceptionConstants.MESSAGE_UNEXPECTED_ERROR;
import static jakarta.ws.rs.core.Response.Status;

import com.ironman.partyreference.application.model.api.ApiExceptionResponse;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Provider
public class UncontrolledExceptionMapper implements ExceptionMapper<Exception> {
  @Override
  public Response toResponse(Exception exception) {
    log.error("Uncontrolled Exception: {}", exception.getMessage(), exception);

    var response =
        new ApiExceptionResponse()
            .description(MESSAGE_UNEXPECTED_ERROR)
            .errorType(ERROR_TYPE_TECHNICAL);

    return Response.status(Status.INTERNAL_SERVER_ERROR).entity(response).build();
  }
}
