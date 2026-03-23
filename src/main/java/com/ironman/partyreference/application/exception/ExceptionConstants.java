package com.ironman.partyreference.application.exception;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ExceptionConstants {
  public static final String MESSAGE_INVALID_INPUT_DATA =
      "Invalid input data. Verify format and values.";
  public static final String MESSAGE_RESOURCE_NOT_FOUND =
      "Resource not found. Verify the identifier.";
  public static final String MESSAGE_INTERNAL_ERROR =
      "Internal server error. Contact administrator.";
  public static final String MESSAGE_BUSINESS_RULE_VIOLATION =
      "Business rule violation. Verify the data does not conflict with existing records.";
  public static final String MESSAGE_UNEXPECTED_ERROR = "Unexpected error. Please try again later.";

  public static final String ERROR_TYPE_FUNCTIONAL = "FUNCTIONAL";
  public static final String ERROR_TYPE_TECHNICAL = "TECHNICAL";

  public static final String MESSAGE_INVALID_DATE_FORMAT =
      "Invalid date format. Please use YYYY-MM-DD format.";

  public static final String COMPONENT_CUSTOMER_SERVICE = "customer-service";
  public static final String COMPONENT_DATABASE_SERVICE = "database-service";
}
