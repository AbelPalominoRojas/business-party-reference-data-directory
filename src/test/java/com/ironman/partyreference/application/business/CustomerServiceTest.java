package com.ironman.partyreference.application.business;

import static com.ironman.partyreference.mock.CustomerMock.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
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
import org.hibernate.exception.ConstraintViolationException;
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

  @Test
  @DisplayName("Should create customer successfully")
  void shouldCreateCustomerSuccessfully() {
    var request = getRegisterPersonRequest();
    var customer = getCustomerTypePerson();
    var response = getRegisterResponse();

    given(partyReferenceTypeResolver.resolveIdentificationTypeCode(any())).willReturn("1");
    given(customerRepository.findByDocumentTypeAndNumber(anyString(), anyString()))
        .willReturn(Optional.empty());
    given(customerMapper.toEntity(any())).willReturn(customer);
    given(customerMapper.toRegisterResponse(isA(CustomerEntity.class))).willReturn(response);

    var result = customerService.createCustomer(request);

    assertNotNull(result);
    assertEquals(response, result);
  }

  @Test
  @DisplayName("Should throw ApplicationException when duplicate identifier on create")
  void shouldThrowApplicationExceptionWhenDuplicateIdentifierOnCreate() {
    var request = getRegisterPersonRequest();
    var duplicateCustomer = getCustomerIdentification(1L);

    given(partyReferenceTypeResolver.resolveIdentificationTypeCode(any())).willReturn("1");
    given(customerRepository.findByDocumentTypeAndNumber(anyString(), anyString()))
        .willReturn(Optional.of(duplicateCustomer));

    assertThrows(ApplicationException.class, () -> customerService.createCustomer(request));
    verify(customerMapper, never()).toEntity(any());
  }

  @Test
  @DisplayName("Should throw ApplicationException when constraint violation on create")
  void shouldThrowApplicationExceptionWhenConstraintViolationOnCreate() {
    var request = getRegisterPersonRequest();
    var customer = getCustomerTypePerson();

    given(partyReferenceTypeResolver.resolveIdentificationTypeCode(any())).willReturn("1");
    given(customerRepository.findByDocumentTypeAndNumber(anyString(), anyString()))
        .willReturn(Optional.empty());
    given(customerMapper.toEntity(any())).willReturn(customer);
    willThrow(new ConstraintViolationException("Constraint violated", null, "uk_document"))
        .given(customerRepository)
        .persist(isA(CustomerEntity.class));

    assertThrows(ApplicationException.class, () -> customerService.createCustomer(request));
  }

  @Test
  @DisplayName("Should throw ApplicationException when database error occurs during create")
  void shouldThrowApplicationExceptionWhenDatabaseErrorOccursDuringCreate() {
    var request = getRegisterPersonRequest();
    var customer = getCustomerTypePerson();

    given(partyReferenceTypeResolver.resolveIdentificationTypeCode(any())).willReturn("1");
    given(customerRepository.findByDocumentTypeAndNumber(anyString(), anyString()))
        .willReturn(Optional.empty());
    given(customerMapper.toEntity(any())).willReturn(customer);
    willThrow(new HibernateException("Connection failed"))
        .given(customerRepository)
        .persist(isA(CustomerEntity.class));

    assertThrows(ApplicationException.class, () -> customerService.createCustomer(request));
  }

  @Test
  @DisplayName("Should update customer successfully")
  void shouldUpdateCustomerSuccessfully() {
    var request = getRegisterPersonRequest();
    var customer = getCustomerTypePerson();
    var response = getRegisterResponse();

    given(customerRepository.findByIdOptional(anyLong())).willReturn(Optional.of(customer));
    given(partyReferenceTypeResolver.resolveIdentificationTypeCode(any())).willReturn("1");
    given(customerRepository.findByDocumentTypeAndNumber(anyString(), anyString()))
        .willReturn(Optional.empty());
    given(customerMapper.toRegisterResponse(isA(CustomerEntity.class))).willReturn(response);

    var result = customerService.updateCustomer(1L, request);

    assertNotNull(result);
    assertEquals(response, result);
  }

  @Test
  @DisplayName("Should throw ApplicationException when customer not found on update")
  void shouldThrowApplicationExceptionWhenCustomerNotFoundOnUpdate() {
    var request = getRegisterPersonRequest();

    given(customerRepository.findByIdOptional(anyLong())).willReturn(Optional.empty());

    assertThrows(ApplicationException.class, () -> customerService.updateCustomer(1L, request));
    verify(customerMapper, never()).toRegisterResponse(isA(CustomerEntity.class));
  }

  @Test
  @DisplayName("Should throw ApplicationException when customer error found on update")
  void shouldThrowApplicationExceptionWhenCustomerErrorFoundOnUpdate() {
    var request = getRegisterPersonRequest();

    given(customerRepository.findByIdOptional(anyLong()))
        .willThrow(new HibernateException("Connection failed"));

    assertThrows(ApplicationException.class, () -> customerService.updateCustomer(1L, request));
    verify(customerMapper, never()).toRegisterResponse(isA(CustomerEntity.class));
  }

  @Test
  @DisplayName("Should update customer when identifier belongs to same customer")
  void shouldUpdateCustomerWhenIdentifierBelongsToSameCustomer() {
    var request = getRegisterPersonRequest();
    var customer = getCustomerTypePerson();
    var response = getRegisterResponse();
    var sameCustomerIdentification = getCustomerIdentification(customer.getId());

    given(customerRepository.findByIdOptional(anyLong())).willReturn(Optional.of(customer));
    given(partyReferenceTypeResolver.resolveIdentificationTypeCode(any())).willReturn("1");
    given(customerRepository.findByDocumentTypeAndNumber(anyString(), anyString()))
        .willReturn(Optional.of(sameCustomerIdentification));
    given(customerMapper.toRegisterResponse(isA(CustomerEntity.class))).willReturn(response);

    var result = customerService.updateCustomer(1L, request);

    assertNotNull(result);
    assertEquals(response, result);
  }

  @Test
  @DisplayName("Should throw ApplicationException when duplicate identifier on update")
  void shouldThrowApplicationExceptionWhenDuplicateIdentifierOnUpdate() {
    var request = getRegisterPersonRequest();
    var customer = getCustomerTypePerson();
    var conflictingCustomerIdentification = getCustomerIdentification(3L);

    given(customerRepository.findByIdOptional(anyLong())).willReturn(Optional.of(customer));
    given(partyReferenceTypeResolver.resolveIdentificationTypeCode(any())).willReturn("1");
    given(customerRepository.findByDocumentTypeAndNumber(anyString(), anyString()))
        .willReturn(Optional.of(conflictingCustomerIdentification));

    assertThrows(ApplicationException.class, () -> customerService.updateCustomer(1L, request));
    verify(customerMapper, never()).toRegisterResponse(isA(CustomerEntity.class));
  }

  @Test
  @DisplayName("Should throw ApplicationException when constraint violation on update")
  void shouldThrowApplicationExceptionWhenConstraintViolationOnUpdate() {
    var request = getRegisterPersonRequest();
    var customer = getCustomerTypePerson();

    given(customerRepository.findByIdOptional(anyLong())).willReturn(Optional.of(customer));
    given(partyReferenceTypeResolver.resolveIdentificationTypeCode(any())).willReturn("1");
    given(customerRepository.findByDocumentTypeAndNumber(anyString(), anyString()))
        .willReturn(Optional.empty());
    willThrow(new ConstraintViolationException("Constraint violated", null, "uk_document"))
        .given(customerRepository)
        .persist(isA(CustomerEntity.class));

    assertThrows(ApplicationException.class, () -> customerService.updateCustomer(1L, request));
  }

  @Test
  @DisplayName("Should throw ApplicationException when database error occurs during update")
  void shouldThrowApplicationExceptionWhenDatabaseErrorOccursDuringUpdate() {
    var request = getRegisterPersonRequest();
    var customer = getCustomerTypePerson();

    given(customerRepository.findByIdOptional(anyLong())).willReturn(Optional.of(customer));
    given(partyReferenceTypeResolver.resolveIdentificationTypeCode(any())).willReturn("1");
    given(customerRepository.findByDocumentTypeAndNumber(anyString(), anyString()))
        .willReturn(Optional.empty());
    willThrow(new HibernateException("Connection failed"))
        .given(customerRepository)
        .persist(isA(CustomerEntity.class));

    assertThrows(ApplicationException.class, () -> customerService.updateCustomer(1L, request));
  }
}
