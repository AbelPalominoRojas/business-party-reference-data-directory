package com.ironman.partyreference.expose.exception;

import static com.ironman.partyreference.application.exception.ApplicationException.ExceptionType;
import static com.ironman.partyreference.application.exception.ExceptionConstants.*;
import static jakarta.ws.rs.core.Response.Status;

import com.ironman.partyreference.application.exception.ApplicationException;
import com.ironman.partyreference.application.model.api.ApiExceptionDetail;
import com.ironman.partyreference.application.model.api.ApiExceptionResponse;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import java.util.List;

@Provider
public class ApplicationExceptionMapper implements ExceptionMapper<ApplicationException> {
  @Override
  public Response toResponse(ApplicationException exception) {
    var detail = createApiExceptionDetail(exception);
    ApiExceptionResponse apiException = createExceptionResponse(exception, List.of(detail));
    Status status = mapToJaxRsExceptionType(exception.getExceptionType());

    return Response.status(status).entity(apiException).build();
  }

  private static ApiExceptionDetail createApiExceptionDetail(ApplicationException exception) {
    return new ApiExceptionDetail()
        .code(exception.getCode())
        .component(exception.getComponent())
        .description(exception.getMessage());
  }

  private ApiExceptionResponse createExceptionResponse(
      ApplicationException exception, List<ApiExceptionDetail> details) {
    String description = descriptionFromExceptionType(exception.getExceptionType());
    String errorType = errorTypeFromExceptionType(exception.getExceptionType());

    return new ApiExceptionResponse()
        .description(description)
        .errorType(errorType)
        .exceptionDetails(details);
  }

  private String descriptionFromExceptionType(ExceptionType exceptionType) {
    return switch (exceptionType) {
      case BAD_REQUEST -> MESSAGE_INVALID_INPUT_DATA;
      case NOT_FOUND -> MESSAGE_RESOURCE_NOT_FOUND;
      case CONFLICT -> MESSAGE_BUSINESS_RULE_VIOLATION;
      case INTERNAL_SERVER_ERROR -> MESSAGE_INTERNAL_ERROR;
      default -> MESSAGE_UNEXPECTED_ERROR;
    };
  }

  private String errorTypeFromExceptionType(ExceptionType exceptionType) {
    return switch (exceptionType) {
      case BAD_REQUEST, NOT_FOUND, CONFLICT -> ERROR_TYPE_FUNCTIONAL;
      case INTERNAL_SERVER_ERROR -> ERROR_TYPE_TECHNICAL;
      default -> "UNKNOWN";
    };
  }

  private Status mapToJaxRsExceptionType(ExceptionType exceptionType) {
    return switch (exceptionType) {
      case BAD_REQUEST -> Status.BAD_REQUEST;
      case NOT_FOUND -> Status.NOT_FOUND;
      default -> Status.INTERNAL_SERVER_ERROR;
    };
  }
}
