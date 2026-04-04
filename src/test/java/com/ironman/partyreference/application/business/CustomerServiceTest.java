package com.ironman.partyreference.application.business;

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
import com.ironman.partyreference.application.model.entity.CustomerEntity;
import com.ironman.partyreference.application.repository.CustomerRepository;
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
}
