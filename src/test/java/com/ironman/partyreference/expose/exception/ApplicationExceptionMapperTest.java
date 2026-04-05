package com.ironman.partyreference.expose.exception;

import static com.ironman.partyreference.application.exception.ApplicationException.ExceptionType;
import static com.ironman.partyreference.application.exception.ApplicationException.ExceptionType.*;
import static com.ironman.partyreference.application.exception.ExceptionConstants.*;
import static org.junit.jupiter.api.Assertions.*;

import com.ironman.partyreference.application.exception.ApplicationException;
import com.ironman.partyreference.application.model.api.ApiExceptionResponse;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class ApplicationExceptionMapperTest {

  private final ApplicationExceptionMapper mapper = new ApplicationExceptionMapper();

  static Stream<Arguments> exceptionTypeProvider() {
    return Stream.of(
        Arguments.of(BAD_REQUEST, 400, MESSAGE_INVALID_INPUT_DATA, ERROR_TYPE_FUNCTIONAL),
        Arguments.of(NOT_FOUND, 404, MESSAGE_RESOURCE_NOT_FOUND, ERROR_TYPE_FUNCTIONAL),
        Arguments.of(CONFLICT, 409, MESSAGE_BUSINESS_RULE_VIOLATION, ERROR_TYPE_FUNCTIONAL),
        Arguments.of(INTERNAL_SERVER_ERROR, 500, MESSAGE_INTERNAL_ERROR, ERROR_TYPE_TECHNICAL));
  }

  @ParameterizedTest(name = "[{index}] {0}")
  @MethodSource("exceptionTypeProvider")
  @DisplayName(
      "Should map ApplicationException to HTTP response with correct status, body and detail")
  void shouldMapApplicationExceptionToResponse(
      ExceptionType exceptionType,
      int expectedStatus,
      String expectedDescription,
      String expectedErrorType) {

    var exception =
        ApplicationException.builder()
            .exceptionType(exceptionType)
            .code("ERR-001")
            .component(COMPONENT_CUSTOMER_SERVICE)
            .message("detail error message")
            .build();

    var response = mapper.toResponse(exception);

    assertEquals(expectedStatus, response.getStatus());

    var body = (ApiExceptionResponse) response.getEntity();
    assertEquals(expectedDescription, body.getDescription());
    assertEquals(expectedErrorType, body.getErrorType());
    assertEquals(1, body.getExceptionDetails().size());

    var detail = body.getExceptionDetails().get(0);
    assertEquals(exception.getCode(), detail.getCode());
    assertEquals(exception.getComponent(), detail.getComponent());
    assertEquals(exception.getMessage(), detail.getDescription());
  }
}
