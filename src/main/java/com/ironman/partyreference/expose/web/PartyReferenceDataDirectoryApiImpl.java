package com.ironman.partyreference.expose.web;

import com.ironman.partyreference.application.business.CustomerService;
import com.ironman.partyreference.application.model.api.*;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@ApplicationScoped
public class PartyReferenceDataDirectoryApiImpl implements PartyReferenceDataDirectoryApi {
  private final CustomerService customerService;

  @Override
  public Response registerPartyReferenceDataDirectoryEntry(
      String requestID,
      RegisterPartyReferenceDataDirectoryEntryRequest
          registerPartyReferenceDataDirectoryEntryRequest) {
    var result = customerService.createCustomer(registerPartyReferenceDataDirectoryEntryRequest);

    return Response.status(Response.Status.CREATED).entity(result).build();
  }

  @Override
  public Response retrievePartyReferenceDataDirectoryEntries(
      String requestID,
      Integer pageNumber,
      Integer pageSize,
      String identifierValue,
      PartyTypeValues partyType,
      ResidencyStatusTypeValues residencyStatus,
      PartyReferenceSortFieldValues sortField,
      SortDirectionValues sortDirection) {

    var query =
        CustomerSearchQuery.builder()
            .pageNumber(pageNumber)
            .pageSize(pageSize)
            .identifierValue(identifierValue)
            .partyType(partyType)
            .residencyStatus(residencyStatus)
            .sortField(sortField)
            .sortDirection(sortDirection)
            .build();

    return Response.ok(customerService.searchCustomers(query)).build();
  }

  @Override
  public Response retrievePartyReferenceDataDirectoryEntry(
      String requestID, Long partyReferenceDataDirectoryEntryId) {
    var result = customerService.getCustomerById(partyReferenceDataDirectoryEntryId);

    if (result.isPresent()) {
      return Response.ok(result.get()).build();
    } else {
      return Response.noContent().build();
    }
  }

  @Override
  public Response updatePartyReferenceDataDirectoryEntry(
      String requestID,
      Long partyReferenceDataDirectoryEntryId,
      RegisterPartyReferenceDataDirectoryEntryRequest
          registerPartyReferenceDataDirectoryEntryRequest) {
    var result =
        customerService.updateCustomer(
            partyReferenceDataDirectoryEntryId, registerPartyReferenceDataDirectoryEntryRequest);

    return Response.ok(result).build();
  }
}
