package com.ironman.partyreference.application.mapper;

import static com.ironman.partyreference.application.mapper.PartyReferenceBuilder.buildNaturalPersonNames;
import static com.ironman.partyreference.application.mapper.PartyReferenceBuilder.buildOrganizationNames;
import static com.ironman.partyreference.application.model.api.DirectoryEntryDateTypeValues.FECHA_CREACION;
import static com.ironman.partyreference.application.model.api.DirectoryEntryDateTypeValues.FECHA_MODIFICACION;
import static com.ironman.partyreference.application.util.AppUtils.buildDirectoryEntryDate;
import static com.ironman.partyreference.application.util.Constants.CUSTOMER_TYPE_NATURAL_PERSON;
import static org.mapstruct.MappingConstants.ComponentModel;

import com.ironman.partyreference.application.model.api.*;
import com.ironman.partyreference.application.model.entity.CustomerEntity;
import com.ironman.partyreference.application.model.entity.projection.CustomerSummaryProjection;
import java.util.List;
import org.mapstruct.*;

@Mapper(
    componentModel = ComponentModel.JAKARTA_CDI,
    uses = {PartyReferenceTypeResolver.class},
    injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface CustomerMapper {

  @Mapping(target = "partyReference", source = ".")
  @Mapping(target = "partyType", source = "customerType")
  @Mapping(target = "residencyStatus", source = "residencyStatus")
  @Mapping(target = "directoryEntryDates", source = ".", qualifiedByName = "mapDirectoryEntryDates")
  RetrievePartyReferenceDataDirectoryEntryResponse toRetrieveResponse(CustomerEntity customer);

  @Mapping(target = "partyId", source = "id")
  @Mapping(target = "partyIdentification", source = ".")
  @Mapping(target = "partyNames", source = ".", qualifiedByName = "mapPartyNamesFromCustomer")
  PartyReferenceWithId toPartyReferenceWithId(CustomerEntity customer);

  @Mapping(target = "partyIdentificationType", source = "documentType")
  @Mapping(target = "partyIdentification", source = ".")
  PartyIdentification toPartyIdentification(CustomerEntity customer);

  @Mapping(target = "identifierValue", source = "documentNumber")
  Identifier toIdentifier(CustomerEntity customer);

  @Mapping(target = "partyReference", source = ".")
  @Mapping(target = "partyType", source = "customerType")
  @Mapping(target = "residencyStatus", source = "residencyStatus")
  PartyReferenceDataDirectoryEntry toDirectoryEntry(CustomerSummaryProjection customer);

  @Mapping(target = "partyId", source = "id")
  @Mapping(target = "partyIdentification", source = ".")
  @Mapping(
      target = "partyNames",
      source = ".",
      qualifiedByName = "mapPartyNamesFromCustomerSummary")
  PartyReferenceWithId toPartyReferenceWithId(CustomerSummaryProjection customer);

  @Mapping(target = "partyIdentificationType", source = "documentType")
  @Mapping(target = "partyIdentification", source = ".")
  PartyIdentification toPartyIdentification(CustomerSummaryProjection customer);

  @Mapping(target = "identifierValue", source = "documentNumber")
  Identifier toIdentifier(CustomerSummaryProjection customer);

  @Named("mapPartyNamesFromCustomer")
  default List<PartyName> mapPartyNamesFromCustomer(CustomerEntity customer) {
    if (CUSTOMER_TYPE_NATURAL_PERSON.equalsIgnoreCase(customer.getCustomerType())) {
      return buildNaturalPersonNames(customer);
    }
    return buildOrganizationNames(customer);
  }

  @Named("mapPartyNamesFromCustomerSummary")
  default List<PartyName> mapPartyNamesFromCustomerSummary(CustomerSummaryProjection customer) {
    if (CUSTOMER_TYPE_NATURAL_PERSON.equalsIgnoreCase(customer.getCustomerType())) {
      return buildNaturalPersonNames(customer);
    }
    return buildOrganizationNames(customer);
  }

  @Named("mapDirectoryEntryDates")
  default List<DirectoryEntryDate> mapDirectoryEntryDates(CustomerEntity customer) {
    return List.of(
        buildDirectoryEntryDate(customer.getCreatedAt(), FECHA_CREACION),
        buildDirectoryEntryDate(customer.getUpdatedAt(), FECHA_MODIFICACION));
  }
}
