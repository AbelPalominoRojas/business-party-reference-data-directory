package com.ironman.partyreference.application.repository;

import static com.ironman.partyreference.mock.CustomerMock.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.*;

import com.ironman.partyreference.application.model.entity.CustomerEntity;
import com.ironman.partyreference.application.model.entity.criteria.CustomerSearchCriteria;
import com.ironman.partyreference.application.model.entity.projection.CustomerIdentificationProjection;
import com.ironman.partyreference.application.model.entity.projection.CustomerSummaryProjection;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Page;
import io.quarkus.panache.common.Sort;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CustomerRepositoryTest {
  @Spy private CustomerRepository customerRepository;

  @Mock private PanacheQuery<CustomerEntity> panacheQuery;

  @Mock private PanacheQuery<CustomerIdentificationProjection> identificationQuery;

  @Mock private PanacheQuery<CustomerSummaryProjection> summaryQuery;

  @Captor private ArgumentCaptor<String> whereClauseCaptor;

  @Captor private ArgumentCaptor<Map<String, Object>> paramsCaptor;

  private static Stream<Arguments> provideSearchCriteria() {
    return Stream.of(
        Arguments.of(getSearchCriteriaWithFilters()),
        Arguments.of(getSearchCriteriaWithoutFilters()));
  }

  @Test
  @DisplayName("Should return projection when customer found by document type and number")
  void shouldReturnProjectionWhenCustomerFound() {
    String documentType = "1";
    String documentNumber = "12345678";
    var expected = getCustomerIdentification(documentType, documentNumber);

    willReturn(panacheQuery).given(customerRepository).find(anyString(), any(Map.class));
    given(panacheQuery.project(CustomerIdentificationProjection.class))
        .willReturn(identificationQuery);
    given(identificationQuery.firstResultOptional()).willReturn(Optional.of(expected));

    var result = customerRepository.findByDocumentTypeAndNumber(documentType, documentNumber);

    assertTrue(result.isPresent());
    assertNotNull(result.get().getId());
    assertEquals(documentType, result.get().getDocumentType());
    assertEquals(documentNumber, result.get().getDocumentNumber());
  }

  @Test
  @DisplayName("Should return empty when customer not found by document type and number")
  void shouldReturnEmptyWhenCustomerNotFound() {
    willReturn(panacheQuery).given(customerRepository).find(anyString(), any(Map.class));
    given(panacheQuery.project(CustomerIdentificationProjection.class))
        .willReturn(identificationQuery);
    given(identificationQuery.firstResultOptional()).willReturn(Optional.empty());

    var result = customerRepository.findByDocumentTypeAndNumber("1", "99999999");

    assertTrue(result.isEmpty());
  }

  @ParameterizedTest
  @MethodSource("provideSearchCriteria")
  @DisplayName("Should build query with correct where clause and parameters based on criteria")
  void shouldBuildQueryWithCorrectWhereClauseAndParameters(CustomerSearchCriteria criteria) {
    var expected = getCustomerSummary();
    var expectedSize = (long) expected.size();

    willReturn(panacheQuery)
        .given(customerRepository)
        .find(anyString(), any(Sort.class), any(Map.class));
    given(panacheQuery.page(any(Page.class))).willReturn(panacheQuery);
    given(panacheQuery.project(CustomerSummaryProjection.class)).willReturn(summaryQuery);
    given(summaryQuery.list()).willReturn(expected);
    given(summaryQuery.count()).willReturn(expectedSize);

    var result = customerRepository.searchCustomers(criteria);

    assertEquals(expectedSize, result.count());
    assertEquals(expected, result.list());
  }

  private void setupSearchCustomersMocks() {
    willReturn(panacheQuery)
        .given(customerRepository)
        .find(anyString(), any(Sort.class), any(Map.class));
    given(panacheQuery.page(any(Page.class))).willReturn(panacheQuery);
    given(panacheQuery.project(CustomerSummaryProjection.class)).willReturn(summaryQuery);
  }

  @Test
  @DisplayName("Should build query with criteria filters")
  void shouldBuildQueryWithCriteriaFilters() {
    var criteria = getSearchCriteriaWithFilters();
    var expectedWhereClause =
        "UPPER(documentNumber) LIKE :documentNumber"
            + " AND customerType = :customerType"
            + " AND residencyStatus = :residencyStatus";
    var documentNumberParam = "%" + criteria.getDocumentNumber() + "%";
    var expectedParams =
        Map.of(
            "documentNumber",
            documentNumberParam,
            "customerType",
            criteria.getCustomerType(),
            "residencyStatus",
            criteria.getResidencyStatus());

    setupSearchCustomersMocks();

    customerRepository.searchCustomers(criteria);

    then(customerRepository)
        .should()
        .find(whereClauseCaptor.capture(), any(Sort.class), paramsCaptor.capture());

    assertEquals(expectedWhereClause, whereClauseCaptor.getValue());
    assertEquals(expectedParams, paramsCaptor.getValue());
  }

  @Test
  @DisplayName("Should build query without criteria filters")
  void shouldBuildQueryWithAllCriteria() {
    var criteria = getSearchCriteriaWithoutFilters();
    var expectedWhereClause = "";
    var expectedParams = Map.of();

    setupSearchCustomersMocks();

    customerRepository.searchCustomers(criteria);

    then(customerRepository)
        .should()
        .find(whereClauseCaptor.capture(), any(Sort.class), paramsCaptor.capture());

    assertEquals(expectedWhereClause, whereClauseCaptor.getValue());
    assertEquals(expectedParams, paramsCaptor.getValue());
  }
}
