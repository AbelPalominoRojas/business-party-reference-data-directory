package com.ironman.partyreference.expose.exception;

import static com.ironman.partyreference.application.exception.ExceptionConstants.ERROR_TYPE_TECHNICAL;
import static com.ironman.partyreference.application.exception.ExceptionConstants.MESSAGE_UNEXPECTED_ERROR;
import static jakarta.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR;
import static org.junit.jupiter.api.Assertions.*;

import com.ironman.partyreference.application.model.api.ApiExceptionResponse;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class UncontrolledExceptionMapperTest {

  private final UncontrolledExceptionMapper mapper = new UncontrolledExceptionMapper();

  static Stream<Arguments> uncontrolledExceptionProvider() {
    return Stream.of(
        Arguments.of(new RuntimeException("runtime failure")),
        Arguments.of(new NullPointerException("null reference")),
        Arguments.of(new IllegalArgumentException("invalid argument")),
        Arguments.of(new IllegalStateException("illegal state")));
  }

  @ParameterizedTest(name = "[{index}] {0}")
  @MethodSource("uncontrolledExceptionProvider")
  @DisplayName("Should map any uncontrolled exception to 500 response with generic technical error")
  void shouldMapUncontrolledExceptionToInternalServerErrorResponse(Exception exception) {
    var response = mapper.toResponse(exception);

    assertEquals(INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());

    var body = (ApiExceptionResponse) response.getEntity();
    assertEquals(MESSAGE_UNEXPECTED_ERROR, body.getDescription());
    assertEquals(ERROR_TYPE_TECHNICAL, body.getErrorType());
    assertTrue(body.getExceptionDetails().isEmpty());
  }
}
