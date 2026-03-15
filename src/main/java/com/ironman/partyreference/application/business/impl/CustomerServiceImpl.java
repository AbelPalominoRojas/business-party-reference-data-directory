package com.ironman.partyreference.application.business.impl;

import com.ironman.partyreference.application.business.CustomerService;
import com.ironman.partyreference.application.mapper.CustomerMapper;
import com.ironman.partyreference.application.model.api.RetrievePartyReferenceDataDirectoryEntryResponse;
import com.ironman.partyreference.application.repository.CustomerRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@ApplicationScoped
public class CustomerServiceImpl implements CustomerService {
  private final CustomerRepository customerRepository;
  private final CustomerMapper customerMapper;

  @Override
  public Optional<RetrievePartyReferenceDataDirectoryEntryResponse> getCustomerById(Long id) {
    return customerRepository.findByIdOptional(id).map(customerMapper::toRetrieveResponse);
  }
}
