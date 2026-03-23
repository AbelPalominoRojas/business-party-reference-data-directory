package com.ironman.partyreference.application.business.impl;

import static com.ironman.partyreference.application.common.pagination.PaginationHelper.*;
import static com.ironman.partyreference.application.exception.ExceptionCatalog.*;
import static com.ironman.partyreference.application.model.entity.enums.CustomerSortableField.resolveEntityFieldName;
import static io.quarkus.panache.common.Sort.Direction;

import com.ironman.partyreference.application.business.CustomerService;
import com.ironman.partyreference.application.exception.ApplicationException;
import com.ironman.partyreference.application.mapper.CustomerMapper;
import com.ironman.partyreference.application.mapper.PartyReferenceTypeResolver;
import com.ironman.partyreference.application.model.api.*;
import com.ironman.partyreference.application.model.entity.CustomerEntity;
import com.ironman.partyreference.application.model.entity.criteria.CustomerSearchCriteria;
import com.ironman.partyreference.application.model.entity.projection.CustomerIdentificationProjection;
import com.ironman.partyreference.application.repository.CustomerRepository;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.HibernateException;
import org.hibernate.exception.ConstraintViolationException;

@Slf4j
@RequiredArgsConstructor
@ApplicationScoped
public class CustomerServiceImpl implements CustomerService {
  private final CustomerRepository customerRepository;
  private final CustomerMapper customerMapper;
  private final PartyReferenceTypeResolver partyReferenceTypeResolver;

  @Override
  public Optional<RetrievePartyReferenceDataDirectoryEntryResponse> getCustomerById(Long id) {
    try {
      return customerRepository.findByIdOptional(id).map(customerMapper::toRetrieveResponse);
    } catch (HibernateException e) {
      log.error("Database error while retrieving customer with id: {}", id, e);
      throw DATABASE_ERROR.buildException();
    }
  }

  @Override
  public RetrievePartyReferenceDataDirectoryEntryListResponse searchCustomers(
      CustomerSearchQuery query) {
    try {
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
    } catch (HibernateException e) {
      log.error("Database error while searching customers with criteria: {}", query, e);
      throw DATABASE_ERROR.buildException();
    }
  }

  @Transactional
  @Override
  public RegisterPartyReferenceDataDirectoryEntryResponse createCustomer(
      RegisterPartyReferenceDataDirectoryEntryRequest request) {
    try {
      validateDuplicateIdentifier(request.getPartyReference().getPartyIdentification(), null);
      var customer = customerMapper.toEntity(request);

      customerRepository.persist(customer);

      return customerMapper.toRegisterResponse(customer);
    } catch (ConstraintViolationException e) {
      log.error("Database constraint violation while creating customer", e);
      throw buildDuplicateIdentifierException(request.getPartyReference().getPartyIdentification());
    } catch (HibernateException e) {
      log.error("Database error while creating customer", e);
      throw DATABASE_ERROR.buildException();
    }
  }

  @Transactional
  @Override
  public RegisterPartyReferenceDataDirectoryEntryResponse updateCustomer(
      Long id, RegisterPartyReferenceDataDirectoryEntryRequest request) {
    var customer = findRequiredCustomer(id);

    try {
      validateDuplicateIdentifier(request.getPartyReference().getPartyIdentification(), id);
      customerMapper.updateEntity(customer, request);
      customerRepository.persist(customer);
      return customerMapper.toRegisterResponse(customer);
    } catch (ConstraintViolationException e) {
      log.error("Database constraint violation while updating customer with id: {}", id, e);
      throw buildDuplicateIdentifierException(request.getPartyReference().getPartyIdentification());
    } catch (HibernateException e) {
      log.error("Database error while updating customer with id: {}", id, e);
      throw DATABASE_ERROR.buildException();
    }
  }

  private CustomerEntity findRequiredCustomer(Long id) {
    try {
      return customerRepository
          .findByIdOptional(id)
          .orElseThrow(() -> CUSTOMER_NOT_EXISTS.buildException(id));
    } catch (HibernateException e) {
      log.error("Database error while retrieving customer with id: {}", id, e);
      throw DATABASE_ERROR.buildException();
    }
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

  public void validateDuplicateIdentifier(
      PartyIdentification partyIdentification, Long currentCustomerId) {
    findCustomerByIdentifier(partyIdentification)
        .filter(existingCustomer -> !existingCustomer.getId().equals(currentCustomerId))
        .ifPresent(
            conflictingCustomer -> {
              throw buildDuplicateIdentifierException(partyIdentification);
            });
  }

  private Optional<CustomerIdentificationProjection> findCustomerByIdentifier(
      PartyIdentification partyIdentification) {
    var identificationType = partyIdentification.getPartyIdentificationType();
    String documentType =
        partyReferenceTypeResolver.resolveIdentificationTypeCode(identificationType);
    String documentNumber = partyIdentification.getPartyIdentification().getIdentifierValue();

    return customerRepository.findByDocumentTypeAndNumber(documentType, documentNumber);
  }

  private ApplicationException buildDuplicateIdentifierException(
      PartyIdentification partyIdentification) {
    var identificationType = partyIdentification.getPartyIdentificationType();
    String documentNumber = partyIdentification.getPartyIdentification().getIdentifierValue();

    return CUSTOMER_DUPLICATE_IDENTIFIER.buildException(identificationType, documentNumber);
  }
}
