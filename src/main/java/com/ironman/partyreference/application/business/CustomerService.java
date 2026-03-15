package com.ironman.partyreference.application.business;

import com.ironman.partyreference.application.model.api.RetrievePartyReferenceDataDirectoryEntryResponse;
import java.util.Optional;

public interface CustomerService {

  Optional<RetrievePartyReferenceDataDirectoryEntryResponse> getCustomerById(Long id);
}
