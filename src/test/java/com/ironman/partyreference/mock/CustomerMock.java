package com.ironman.partyreference.mock;

import com.fasterxml.jackson.core.type.TypeReference;
import com.ironman.partyreference.application.model.api.RetrievePartyReferenceDataDirectoryEntryResponse;
import com.ironman.partyreference.application.model.entity.CustomerEntity;
import com.ironman.partyreference.application.model.entity.criteria.CustomerSearchCriteria;
import com.ironman.partyreference.application.model.entity.projection.CustomerIdentificationProjection;
import com.ironman.partyreference.application.model.entity.projection.CustomerSummaryProjection;
import com.ironman.partyreference.util.JsonFileReader;
import io.quarkus.panache.common.Page;
import io.quarkus.panache.common.Sort;
import java.util.List;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CustomerMock {

  public static CustomerEntity getCustomerTypePerson() {
    return JsonFileReader.read("mock/database/customer_type_person.json", new TypeReference<>() {});
  }

  public static CustomerEntity getCustomerTypeOrganization() {
    return JsonFileReader.read(
        "mock/database/customer_type_organization.json", new TypeReference<>() {});
  }

  public static List<CustomerSummaryProjection> getCustomerSummary() {
    return JsonFileReader.read(
        "mock/database/customer_summary_projection.json", new TypeReference<>() {});
  }

  public static RetrievePartyReferenceDataDirectoryEntryResponse getPartyReferencePerson() {
    return JsonFileReader.read(
        "mock/api/retrieve_party_reference_data_directory_entry_person_response.json",
        new TypeReference<>() {});
  }

  public static RetrievePartyReferenceDataDirectoryEntryResponse getPartyReferenceOrganization() {
    return JsonFileReader.read(
        "mock/api/retrieve_party_reference_data_directory_entry_organization_response.json",
        new TypeReference<>() {});
  }

  public static CustomerIdentificationProjection getCustomerIdentification(
      String documentType, String documentNumber) {
    return CustomerIdentificationProjection.builder()
        .id(1L)
        .documentType("1")
        .documentNumber("12345678")
        .build();
  }

  public static CustomerSearchCriteria getSearchCriteriaWithFilters() {
    return CustomerSearchCriteria.builder()
        .documentNumber("123")
        .customerType("P")
        .residencyStatus("N")
        .page(Page.of(0, 10))
        .sort(Sort.by("documentNumber").descending())
        .build();
  }

  public static CustomerSearchCriteria getSearchCriteriaWithoutFilters() {
    return CustomerSearchCriteria.builder()
        .page(Page.of(0, 10))
        .sort(Sort.by("documentNumber").descending())
        .build();
  }
}
