package com.ironman.partyreference.application.business;

import static com.ironman.partyreference.mock.CustomerMock.getCustomerSummary;
import static com.ironman.partyreference.mock.CustomerMock.getPartyReferencePerson;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import com.ironman.partyreference.application.business.impl.CustomerServiceImpl;
import com.ironman.partyreference.application.exception.ApplicationException;
import com.ironman.partyreference.application.mapper.CustomerMapper;
import com.ironman.partyreference.application.mapper.PartyReferenceTypeResolver;
import com.ironman.partyreference.application.model.api.CustomerSearchQuery;
import com.ironman.partyreference.application.model.api.PartyReferenceDataDirectoryEntry;
import com.ironman.partyreference.application.model.entity.CustomerEntity;
import com.ironman.partyreference.application.model.entity.criteria.CustomerSearchCriteria;
import com.ironman.partyreference.application.model.entity.projection.CustomerSummaryProjection;
import com.ironman.partyreference.application.repository.CustomerRepository;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Page;
import java.util.List;
import java.util.Optional;
import org.hibernate.HibernateException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

  @Mock private CustomerRepository customerRepository;

  @Mock private CustomerMapper customerMapper;

  @Mock private PartyReferenceTypeResolver partyReferenceTypeResolver;

  @Mock private PanacheQuery<CustomerSummaryProjection> panacheQuery;

  @InjectMocks private CustomerServiceImpl customerService;

  @Test
  @DisplayName("Should return customer when found")
  void shouldReturnCustomerWhenFound() {
    var partyReference = getPartyReferencePerson();
    given(customerRepository.findByIdOptional(anyLong()))
        .willReturn(Optional.of(new CustomerEntity()));
    given(customerMapper.toRetrieveResponse(isA(CustomerEntity.class))).willReturn(partyReference);

    var result = customerService.getCustomerById(1L);

    assertTrue(result.isPresent());
    assertEquals(partyReference, result.get());
  }

  @Test
  @DisplayName("Should return empty when customer not found")
  void shouldReturnEmptyWhenCustomerNotFound() {
    given(customerRepository.findByIdOptional(anyLong())).willReturn(Optional.empty());

    var result = customerService.getCustomerById(1L);

    assertTrue(result.isEmpty());
    verify(customerMapper, never()).toRetrieveResponse(isA(CustomerEntity.class));
  }

  @Test
  @DisplayName("Should throw ApplicationException when database error occurs")
  void shouldThrowApplicationExceptionWhenDatabaseErrorOccurs() {
    given(customerRepository.findByIdOptional(anyLong()))
        .willThrow(new HibernateException("Connection failed"));

    assertThrows(ApplicationException.class, () -> customerService.getCustomerById(1L));
    verify(customerMapper, never()).toRetrieveResponse(isA(CustomerEntity.class));
  }

  @Test
  @DisplayName("Should return paginated customers when search with filters")
  void shouldReturnPaginatedCustomersWhenSearchWithFilters() {
    List<CustomerSummaryProjection> customers = getCustomerSummary();
    var page = Page.of(0, 10);

    given(partyReferenceTypeResolver.resolvePartyTypeCode(any())).willReturn("P");
    given(partyReferenceTypeResolver.resolveResidencyStatusCode(any())).willReturn("N");
    given(customerRepository.searchCustomers(any(CustomerSearchCriteria.class)))
        .willReturn(panacheQuery);
    given(panacheQuery.stream()).willReturn(customers.stream());
    given(panacheQuery.page()).willReturn(page);
    given(panacheQuery.count()).willReturn((long) customers.size());
    given(panacheQuery.pageCount()).willReturn(1);
    given(customerMapper.toDirectoryEntry(isA(CustomerSummaryProjection.class)))
        .willReturn(new PartyReferenceDataDirectoryEntry());

    var query =
        CustomerSearchQuery.builder()
            .identifierValue("123")
            .partyType(null)
            .residencyStatus(null)
            .pageNumber(1)
            .pageSize(10)
            .build();

    var result = customerService.searchCustomers(query);

    assertNotNull(result);
    assertNotNull(result.getData());
    assertFalse(result.getData().isEmpty());
    assertEquals(customers.size(), result.getData().size());
    assertNotNull(result.getPagination());
  }

  @Test
  @DisplayName("Should return empty data when no customers match search criteria")
  void shouldReturnEmptyDataWhenNoCustomersMatchSearchCriteria() {
    var page = Page.of(0, 10);

    given(partyReferenceTypeResolver.resolvePartyTypeCode(any())).willReturn(null);
    given(partyReferenceTypeResolver.resolveResidencyStatusCode(any())).willReturn(null);
    given(customerRepository.searchCustomers(any(CustomerSearchCriteria.class)))
        .willReturn(panacheQuery);
    given(panacheQuery.stream()).willReturn(java.util.stream.Stream.empty());
    given(panacheQuery.page()).willReturn(page);
    given(panacheQuery.count()).willReturn(0L);
    given(panacheQuery.pageCount()).willReturn(0);

    var query = CustomerSearchQuery.builder().pageNumber(1).pageSize(10).build();

    var result = customerService.searchCustomers(query);

    assertNotNull(result);
    assertTrue(result.getData().isEmpty());
    verify(customerMapper, never()).toDirectoryEntry(isA(CustomerSummaryProjection.class));
  }

  @Test
  @DisplayName("Should throw ApplicationException when database error occurs during search")
  void shouldThrowApplicationExceptionWhenDatabaseErrorOccursDuringSearch() {
    given(partyReferenceTypeResolver.resolvePartyTypeCode(any())).willReturn(null);
    given(partyReferenceTypeResolver.resolveResidencyStatusCode(any())).willReturn(null);
    given(customerRepository.searchCustomers(any(CustomerSearchCriteria.class)))
        .willThrow(new HibernateException("Connection failed"));

    var query = CustomerSearchQuery.builder().pageNumber(1).pageSize(10).build();

    assertThrows(ApplicationException.class, () -> customerService.searchCustomers(query));
    verify(customerMapper, never()).toDirectoryEntry(isA(CustomerSummaryProjection.class));
  }
}
