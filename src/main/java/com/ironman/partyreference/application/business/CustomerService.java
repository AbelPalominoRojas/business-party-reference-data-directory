package com.ironman.partyreference.application.business;

import com.ironman.partyreference.application.model.api.*;
import java.util.Optional;

public interface CustomerService {

  Optional<RetrievePartyReferenceDataDirectoryEntryResponse> getCustomerById(Long id);

  RetrievePartyReferenceDataDirectoryEntryListResponse searchCustomers(CustomerSearchQuery query);

  RegisterPartyReferenceDataDirectoryEntryResponse createCustomer(
      RegisterPartyReferenceDataDirectoryEntryRequest request);

  RegisterPartyReferenceDataDirectoryEntryResponse updateCustomer(
      Long id, RegisterPartyReferenceDataDirectoryEntryRequest request);
}
