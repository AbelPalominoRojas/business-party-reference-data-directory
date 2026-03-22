package com.ironman.partyreference.application.business.impl;

import static com.ironman.partyreference.application.common.pagination.PaginationHelper.*;
import static com.ironman.partyreference.application.model.entity.enums.CustomerSortableField.resolveEntityFieldName;
import static io.quarkus.panache.common.Sort.Direction;

import com.ironman.partyreference.application.business.CustomerService;
import com.ironman.partyreference.application.mapper.CustomerMapper;
import com.ironman.partyreference.application.mapper.PartyReferenceTypeResolver;
import com.ironman.partyreference.application.model.api.*;
import com.ironman.partyreference.application.model.entity.criteria.CustomerSearchCriteria;
import com.ironman.partyreference.application.repository.CustomerRepository;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@ApplicationScoped
public class CustomerServiceImpl implements CustomerService {
  private final CustomerRepository customerRepository;
  private final CustomerMapper customerMapper;
  private final PartyReferenceTypeResolver partyReferenceTypeResolver;

  @Override
  public Optional<RetrievePartyReferenceDataDirectoryEntryResponse> getCustomerById(Long id) {
    return customerRepository.findByIdOptional(id).map(customerMapper::toRetrieveResponse);
  }

  @Override
  public RetrievePartyReferenceDataDirectoryEntryListResponse searchCustomers(
      CustomerSearchQuery query) {
    var codePartyType = partyReferenceTypeResolver.resolvePartyTypeCode(query.getPartyType());
    var codeResidencyStatus =
        partyReferenceTypeResolver.resolveResidencyStatusCode(query.getResidencyStatus());

    var page = toPage(query);
    Sort sort = resolveSortFromQuery(query);

    var criteria =
        CustomerSearchCriteria.builder()
            .documentNumber(query.getIdentifierValue())
            .customerType(codePartyType)
            .residencyStatus(codeResidencyStatus)
            .page(page)
            .sort(sort)
            .build();

    var panacheQuery = customerRepository.searchCustomers(criteria);

    var result = toPaginatedResult(panacheQuery, customerMapper::toDirectoryEntry);

    return new RetrievePartyReferenceDataDirectoryEntryListResponse()
        .data(result.getData())
        .pagination(result.getPagination());
  }

  @Transactional
  @Override
  public RegisterPartyReferenceDataDirectoryEntryResponse createCustomer(
      RegisterPartyReferenceDataDirectoryEntryRequest request) {

    var customer = customerMapper.toEntity(request);

    customerRepository.persist(customer);

    return customerMapper.toRegisterResponse(customer);
  }

  @Transactional
  @Override
  public RegisterPartyReferenceDataDirectoryEntryResponse updateCustomer(
      Long id, RegisterPartyReferenceDataDirectoryEntryRequest request) {

    var customer =
        customerRepository
            .findByIdOptional(id)
            .orElseThrow(() -> new RuntimeException("Customer not found"));

    customerMapper.updateEntity(customer, request);
    customerRepository.persist(customer);

    return customerMapper.toRegisterResponse(customer);
  }

  private static Sort resolveSortFromQuery(CustomerSearchQuery query) {
    Direction direction = resolveDirection(query.getSortDirection());

    String apiFieldName =
        Optional.ofNullable(query.getSortField())
            .map(PartyReferenceSortFieldValues::toString)
            .orElse(null);

    String fieldName = resolveEntityFieldName(apiFieldName);
    return Sort.by(fieldName, direction);
  }
}
