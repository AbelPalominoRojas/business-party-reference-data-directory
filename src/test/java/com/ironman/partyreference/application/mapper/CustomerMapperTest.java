package com.ironman.partyreference.application.mapper;

import static com.ironman.partyreference.application.model.api.DirectoryEntryDateTypeValues.FECHA_CREACION;
import static com.ironman.partyreference.application.model.api.DirectoryEntryDateTypeValues.FECHA_MODIFICACION;
import static com.ironman.partyreference.application.model.api.PartyIdentificationTypeValues.DOCUMENTO_NACIONAL_IDENTIDAD;
import static com.ironman.partyreference.application.model.api.PartyNameTypeValues.*;
import static com.ironman.partyreference.application.model.api.PartyTypeValues.PERSONA;
import static com.ironman.partyreference.application.util.AppUtils.findNameByType;
import static com.ironman.partyreference.application.util.AppUtils.joinNonBlankWith;
import static com.ironman.partyreference.mock.CustomerMock.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;

import com.ironman.partyreference.application.model.api.PartyIdentificationTypeValues;
import com.ironman.partyreference.application.model.api.PartyTypeValues;
import com.ironman.partyreference.application.model.api.RegisterPartyReferenceDataDirectoryEntryRequest;
import com.ironman.partyreference.application.model.api.ResidencyStatusTypeValues;
import com.ironman.partyreference.application.model.entity.CustomerEntity;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CustomerMapperTest {

  @Mock private PartyReferenceTypeResolver partyReferenceTypeResolver;

  @InjectMocks private CustomerMapperImpl customerMapper;

  @Test
  @DisplayName("Should map person CustomerEntity to retrieve response with names and dates")
  void shouldMapPersonEntityToRetrieveResponse() {
    var customer = getCustomerTypePerson();

    given(partyReferenceTypeResolver.resolveIdentificationType(anyString()))
        .willReturn(DOCUMENTO_NACIONAL_IDENTIDAD);
    given(partyReferenceTypeResolver.resolvePartyType(anyString()))
        .willReturn(PartyTypeValues.PERSONA);
    given(partyReferenceTypeResolver.resolveResidencyStatus(anyString()))
        .willReturn(ResidencyStatusTypeValues.NACIONAL);

    var result = customerMapper.toRetrieveResponse(customer);

    assertNotNull(result);

    var partyRef = result.getPartyReference();
    assertNotNull(partyRef);
    assertEquals(String.valueOf(customer.getId()), partyRef.getPartyId());

    var identification = partyRef.getPartyIdentification();
    assertEquals(DOCUMENTO_NACIONAL_IDENTIDAD, identification.getPartyIdentificationType());

    var identifierValue = identification.getPartyIdentification().getIdentifierValue();
    assertEquals(customer.getDocumentNumber(), identifierValue);

    var names = partyRef.getPartyNames();
    assertEquals(4, names.size());
    assertEquals(customer.getName(), findNameByType(names, NOMBRE));
    assertEquals(customer.getPaternalSurname(), findNameByType(names, APELLIDO_PATERNO));
    assertEquals(customer.getMaternalSurname(), findNameByType(names, APELLIDO_MATERNO));

    var fullName =
        joinNonBlankWith(
            " ", customer.getName(), customer.getPaternalSurname(), customer.getMaternalSurname());
    assertEquals(fullName, findNameByType(names, NOMBRE_COMPLETO));

    assertEquals(PartyTypeValues.PERSONA, result.getPartyType());
    assertEquals(ResidencyStatusTypeValues.NACIONAL, result.getResidencyStatus());

    var dates = result.getDirectoryEntryDates();
    assertEquals(2, dates.size());
    assertEquals(FECHA_CREACION, dates.get(0).getDirectoryEntryDateType());
    assertEquals(customer.getCreatedAt(), dates.get(0).getDirectoryEntryDate());
    assertEquals(FECHA_MODIFICACION, dates.get(1).getDirectoryEntryDateType());
    assertEquals(customer.getUpdatedAt(), dates.get(1).getDirectoryEntryDate());
  }

  @Test
  @DisplayName(
      "Should map organization CustomerEntity to retrieve response with organization names")
  void shouldMapOrganizationEntityToRetrieveResponse() {
    var customer = getCustomerTypeOrganization();

    given(partyReferenceTypeResolver.resolveIdentificationType(anyString()))
        .willReturn(PartyIdentificationTypeValues.REGISTRO_UNICO_CONTRIBUYENTE);
    given(partyReferenceTypeResolver.resolvePartyType(anyString()))
        .willReturn(PartyTypeValues.ORGANIZACION);
    given(partyReferenceTypeResolver.resolveResidencyStatus(anyString()))
        .willReturn(ResidencyStatusTypeValues.NACIONAL);

    var result = customerMapper.toRetrieveResponse(customer);

    assertNotNull(result);

    var partyRef = result.getPartyReference();
    assertEquals(String.valueOf(customer.getId()), partyRef.getPartyId());

    var identifierValue =
        partyRef.getPartyIdentification().getPartyIdentification().getIdentifierValue();
    assertEquals(customer.getDocumentNumber(), identifierValue);

    var names = partyRef.getPartyNames();
    assertEquals(2, names.size());
    assertEquals(customer.getName(), findNameByType(names, RAZON_SOCIAL));
    assertEquals(customer.getTradeName(), findNameByType(names, NOMBRE_FANTASIA));

    assertEquals(PartyTypeValues.ORGANIZACION, result.getPartyType());
  }

  @Test
  @DisplayName("Should map person CustomerSummaryProjection to directory entry")
  void shouldMapPersonSummaryProjectionToDirectoryEntry() {
    var customer = getCustomerSummary().get(0);

    given(partyReferenceTypeResolver.resolveIdentificationType(anyString()))
        .willReturn(DOCUMENTO_NACIONAL_IDENTIDAD);
    given(partyReferenceTypeResolver.resolvePartyType(anyString()))
        .willReturn(PartyTypeValues.PERSONA);
    given(partyReferenceTypeResolver.resolveResidencyStatus(anyString()))
        .willReturn(ResidencyStatusTypeValues.NACIONAL);

    var result = customerMapper.toDirectoryEntry(customer);

    assertNotNull(result);

    var partyRef = result.getPartyReference();
    assertEquals(String.valueOf(customer.getId()), partyRef.getPartyId());

    var identifierValue =
        partyRef.getPartyIdentification().getPartyIdentification().getIdentifierValue();
    assertEquals(customer.getDocumentNumber(), identifierValue);

    var names = partyRef.getPartyNames();
    assertEquals(4, names.size());
    assertEquals(customer.getName(), findNameByType(names, NOMBRE));
    assertEquals(customer.getPaternalSurname(), findNameByType(names, APELLIDO_PATERNO));
    assertEquals(customer.getMaternalSurname(), findNameByType(names, APELLIDO_MATERNO));

    var fullName =
        joinNonBlankWith(
            " ", customer.getName(), customer.getPaternalSurname(), customer.getMaternalSurname());
    assertEquals(fullName, findNameByType(names, NOMBRE_COMPLETO));

    assertEquals(PartyTypeValues.PERSONA, result.getPartyType());
    assertEquals(ResidencyStatusTypeValues.NACIONAL, result.getResidencyStatus());
  }

  @Test
  @DisplayName("Should map organization CustomerSummaryProjection to directory entry")
  void shouldMapOrganizationSummaryProjectionToDirectoryEntry() {
    var customer = getCustomerSummary().get(1);

    given(partyReferenceTypeResolver.resolveIdentificationType(anyString()))
        .willReturn(PartyIdentificationTypeValues.REGISTRO_UNICO_CONTRIBUYENTE);
    given(partyReferenceTypeResolver.resolvePartyType(anyString()))
        .willReturn(PartyTypeValues.ORGANIZACION);
    given(partyReferenceTypeResolver.resolveResidencyStatus(anyString()))
        .willReturn(ResidencyStatusTypeValues.NACIONAL);

    var result = customerMapper.toDirectoryEntry(customer);

    assertNotNull(result);

    var names = result.getPartyReference().getPartyNames();
    assertEquals(2, names.size());
    assertEquals(customer.getName(), findNameByType(names, RAZON_SOCIAL));
    assertEquals(customer.getTradeName(), findNameByType(names, NOMBRE_FANTASIA));

    assertEquals(PartyTypeValues.ORGANIZACION, result.getPartyType());
  }

  static Stream<Arguments> toEntityProvider() {
    return Stream.of(
        Arguments.of("person request", getRegisterPersonRequest(), "1", "P", "N"),
        Arguments.of("organization request", getRegisterOrganizationRequest(), "6", "O", "N"));
  }

  @ParameterizedTest(name = "[{index}] {0}")
  @MethodSource("toEntityProvider")
  @DisplayName("Should map request to CustomerEntity")
  void shouldMapRequestToEntity(
      String displayName,
      RegisterPartyReferenceDataDirectoryEntryRequest request,
      String expectedDocumentType,
      String expectedCustomerType,
      String expectedResidencyStatus) {

    given(partyReferenceTypeResolver.resolveIdentificationTypeCode(any()))
        .willReturn(expectedDocumentType);
    given(partyReferenceTypeResolver.resolvePartyTypeCode(any())).willReturn(expectedCustomerType);
    given(partyReferenceTypeResolver.resolveResidencyStatusCode(any()))
        .willReturn(expectedResidencyStatus);

    var result = customerMapper.toEntity(request);

    assertNotNull(result);
    assertEquals(expectedDocumentType, result.getDocumentType());

    var partyReference = request.getPartyReference();
    var identifierValue =
        partyReference.getPartyIdentification().getPartyIdentification().getIdentifierValue();
    assertEquals(identifierValue, result.getDocumentNumber());

    assertEquals(expectedCustomerType, result.getCustomerType());
    assertEquals(expectedResidencyStatus, result.getResidencyStatus());

    var partyNames = partyReference.getPartyNames();
    var nameType = (PERSONA == request.getPartyType()) ? NOMBRE : RAZON_SOCIAL;
    assertEquals(findNameByType(partyNames, nameType), result.getName());
    assertEquals(findNameByType(partyNames, APELLIDO_PATERNO), result.getPaternalSurname());
    assertEquals(findNameByType(partyNames, APELLIDO_MATERNO), result.getMaternalSurname());
    assertEquals(findNameByType(partyNames, NOMBRE_FANTASIA), result.getTradeName());
  }

  static Stream<Arguments> updateEntityProvider() {
    return Stream.of(
        Arguments.of(
            "person", getCustomerTypeOrganization(), getRegisterPersonRequest(), "1", "P", "N"),
        Arguments.of(
            "organization",
            getCustomerTypePerson(),
            getRegisterOrganizationRequest(),
            "6",
            "O",
            "N"));
  }

  @ParameterizedTest(name = "[{index}] {0}")
  @MethodSource("updateEntityProvider")
  @DisplayName("Should update CustomerEntity from request preserving entity id")
  void shouldUpdateEntity(
      String displayName,
      CustomerEntity customer,
      RegisterPartyReferenceDataDirectoryEntryRequest request,
      String expectedDocumentType,
      String expectedCustomerType,
      String expectedResidencyStatus) {

    given(partyReferenceTypeResolver.resolveIdentificationTypeCode(any()))
        .willReturn(expectedDocumentType);
    given(partyReferenceTypeResolver.resolvePartyTypeCode(any())).willReturn(expectedCustomerType);
    given(partyReferenceTypeResolver.resolveResidencyStatusCode(any()))
        .willReturn(expectedResidencyStatus);

    customerMapper.updateEntity(customer, request);

    assertNotNull(customer.getId());

    assertEquals(expectedDocumentType, customer.getDocumentType());

    var partyReference = request.getPartyReference();
    var identifierValue =
        partyReference.getPartyIdentification().getPartyIdentification().getIdentifierValue();
    assertEquals(identifierValue, customer.getDocumentNumber());

    assertEquals(expectedCustomerType, customer.getCustomerType());
    assertEquals(expectedResidencyStatus, customer.getResidencyStatus());

    var partyNames = partyReference.getPartyNames();
    var nameType = (PERSONA == request.getPartyType()) ? NOMBRE : RAZON_SOCIAL;
    assertEquals(findNameByType(partyNames, nameType), customer.getName());
    assertEquals(findNameByType(partyNames, APELLIDO_PATERNO), customer.getPaternalSurname());
    assertEquals(findNameByType(partyNames, APELLIDO_MATERNO), customer.getMaternalSurname());
    assertEquals(findNameByType(partyNames, NOMBRE_FANTASIA), customer.getTradeName());
  }

  @Test
  @DisplayName("Should map CustomerEntity to register response with party id")
  void shouldMapEntityToRegisterResponse() {
    var customer = getCustomerTypePerson();

    var result = customerMapper.toRegisterResponse(customer);

    assertNotNull(result);
    assertNotNull(result.getPartyReference());
    assertEquals(String.valueOf(customer.getId()), result.getPartyReference().getPartyId());
  }
}
