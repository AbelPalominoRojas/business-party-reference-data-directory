package com.ironman.partyreference.application.model.entity.enums;

import java.util.Arrays;
import java.util.Optional;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum CustomerSortableField {
  ID("partyId", "id", "customer_id"),
  DOCUMENT_NUMBER("identifierValue", "documentNumber", "document_number"),
  CUSTOMER_TYPE("partyType", "customerType", "customer_type"),
  RESIDENCY_STATUS("residencyStatus", "residencyStatus", "residency_status");

  private final String apiFieldName;
  private final String entityFieldName;
  private final String columnName;

  private static Optional<CustomerSortableField> findByApiFieldName(String apiFieldName) {
    return Arrays.stream(values())
        .filter(field -> field.getApiFieldName().equals(apiFieldName))
        .findFirst();
  }

  public static String resolveEntityFieldName(String apiFieldName) {
    return findByApiFieldName(apiFieldName)
        .map(CustomerSortableField::getEntityFieldName)
        .orElse(ID.getEntityFieldName());
  }

  public static String resolveColumnName(String apiFieldName) {
    return findByApiFieldName(apiFieldName)
        .map(CustomerSortableField::getColumnName)
        .orElse(ID.getColumnName());
  }
}
