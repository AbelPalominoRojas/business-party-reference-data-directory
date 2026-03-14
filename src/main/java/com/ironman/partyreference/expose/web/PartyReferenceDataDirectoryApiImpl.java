package com.ironman.partyreference.expose.web;

import com.ironman.partyreference.application.model.api.*;
import jakarta.ws.rs.core.Response;

public class PartyReferenceDataDirectoryApiImpl implements PartyReferenceDataDirectoryApi {

    @Override
    public Response registerPartyReferenceDataDirectoryEntry(
            String requestID,
            RegisterPartyReferenceDataDirectoryEntryRequest registerPartyReferenceDataDirectoryEntryRequest
    ) {
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
            SortDirectionValues sortDirection
    ) {
        return null;
    }

    @Override
    public Response retrievePartyReferenceDataDirectoryEntry(
            String requestID,
            Long partyReferenceDataDirectoryEntryId
    ) {
        return null;
    }

    @Override
    public Response updatePartyReferenceDataDirectoryEntry(
            String requestID,
            Long partyReferenceDataDirectoryEntryId,
            RegisterPartyReferenceDataDirectoryEntryRequest registerPartyReferenceDataDirectoryEntryRequest
    ) {
        return null;
    }
}
