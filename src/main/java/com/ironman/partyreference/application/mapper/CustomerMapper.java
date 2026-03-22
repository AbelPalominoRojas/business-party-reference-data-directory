package com.ironman.partyreference.application.mapper;

import static com.ironman.partyreference.application.mapper.PartyReferenceBuilder.buildNaturalPersonNames;
import static com.ironman.partyreference.application.mapper.PartyReferenceBuilder.buildOrganizationNames;
import static com.ironman.partyreference.application.model.api.DirectoryEntryDateTypeValues.FECHA_CREACION;
import static com.ironman.partyreference.application.model.api.DirectoryEntryDateTypeValues.FECHA_MODIFICACION;
import static com.ironman.partyreference.application.model.api.PartyNameTypeValues.*;
import static com.ironman.partyreference.application.model.api.PartyTypeValues.PERSONA;
import static com.ironman.partyreference.application.util.AppUtils.buildDirectoryEntryDate;
import static com.ironman.partyreference.application.util.AppUtils.findNameByType;
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

  @Mapping(target = "partyReference.partyId", source = "id")
  RegisterPartyReferenceDataDirectoryEntryResponse toRegisterResponse(CustomerEntity customer);

  @Mapping(
      target = "documentType",
      source = "partyReference.partyIdentification.partyIdentificationType")
  @Mapping(
      target = "documentNumber",
      source = "partyReference.partyIdentification.partyIdentification.identifierValue")
  @Mapping(target = "name", ignore = true)
  @Mapping(target = "paternalSurname", ignore = true)
  @Mapping(target = "maternalSurname", ignore = true)
  @Mapping(target = "tradeName", ignore = true)
  @Mapping(target = "customerType", source = "partyType")
  @Mapping(target = "residencyStatus", source = "residencyStatus")
  CustomerEntity toEntity(RegisterPartyReferenceDataDirectoryEntryRequest request);

  @InheritConfiguration(name = "toEntity")
  void updateEntity(
      @MappingTarget CustomerEntity customer,
      RegisterPartyReferenceDataDirectoryEntryRequest request);

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

  @AfterMapping
  default void mapNameFields(
      RegisterPartyReferenceDataDirectoryEntryRequest request,
      @MappingTarget CustomerEntity customer) {
    var partyNames = request.getPartyReference().getPartyNames();

    var nameType = (PERSONA == request.getPartyType()) ? NOMBRE : RAZON_SOCIAL;
    customer.setName(findNameByType(partyNames, nameType));

    customer.setPaternalSurname(findNameByType(partyNames, APELLIDO_PATERNO));
    customer.setMaternalSurname(findNameByType(partyNames, APELLIDO_MATERNO));
    customer.setTradeName(findNameByType(partyNames, NOMBRE_FANTASIA));
  }
}
