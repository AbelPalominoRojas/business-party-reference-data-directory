package com.ironman.partyreference.application.exception;

import static com.ironman.partyreference.application.exception.ApplicationException.ExceptionType;
import static com.ironman.partyreference.application.exception.ExceptionConstants.COMPONENT_CUSTOMER_SERVICE;
import static com.ironman.partyreference.application.exception.ExceptionConstants.COMPONENT_DATABASE_SERVICE;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ExceptionCatalog {
  DATABASE_ERROR(
      "BPRDD0001",
      ExceptionType.INTERNAL_SERVER_ERROR,
      COMPONENT_DATABASE_SERVICE,
      "An unexpected error occurred in the database service."),
  APPLICATION_ERROR(
      "BPRDD0002",
      ExceptionType.INTERNAL_SERVER_ERROR,
      COMPONENT_CUSTOMER_SERVICE,
      "An unexpected error occurred, please try again later."),
  CUSTOMER_NOT_FOUND(
      "BPRDD0003",
      ExceptionType.NOT_FOUND,
      COMPONENT_CUSTOMER_SERVICE,
      "Customer not found with id: %s"),
  CUSTOMER_DUPLICATE_IDENTIFIER(
      "BPRDD0004",
      ExceptionType.CONFLICT,
      COMPONENT_CUSTOMER_SERVICE,
      "Duplicate customer entry: identification type '%s' with value '%s' already exists.");

  private final String code;
  private final ExceptionType exceptionType;
  private final String component;
  private final String message;

  public ApplicationException buildException(Object... args) {
    String formattedMessage = String.format(message, args);

    return ApplicationException.builder()
        .code(code)
        .exceptionType(exceptionType)
        .component(component)
        .message(formattedMessage)
        .build();
  }
}
