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
    return null;
  }

  @Override
  public Response retrievePartyReferenceDataDirectoryEntries(
      String requestID,
      Integer pageNumber,
      Integer pageSize,
      String identifierValue,
      PartyTypeValues partyType,
      ResidencyStatusTypeValues residencyStatus,
      SortFieldValues sortField,
      SortDirectionValues sortDirection) {
    return null;
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
    return null;
  }
}
