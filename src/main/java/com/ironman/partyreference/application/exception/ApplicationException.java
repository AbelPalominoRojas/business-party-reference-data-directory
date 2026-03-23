package com.ironman.partyreference.application.exception;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@Builder
public class ApplicationException extends RuntimeException {
  private String code;
  private ExceptionType exceptionType;
  private String component;
  private String message;

  @RequiredArgsConstructor
  @Getter
  public enum ExceptionType {
    BAD_REQUEST(400),
    NOT_FOUND(404),
    CONFLICT(409),
    INTERNAL_SERVER_ERROR(500);

    private final int statusCode;
  }
}
