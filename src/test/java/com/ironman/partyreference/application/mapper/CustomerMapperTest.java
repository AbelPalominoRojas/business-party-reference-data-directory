package com.ironman.partyreference.application.mapper;

import static com.ironman.partyreference.application.model.api.DirectoryEntryDateTypeValues.FECHA_CREACION;
import static com.ironman.partyreference.application.model.api.DirectoryEntryDateTypeValues.FECHA_MODIFICACION;
import static com.ironman.partyreference.application.model.api.PartyIdentificationTypeValues.DOCUMENTO_NACIONAL_IDENTIDAD;
import static com.ironman.partyreference.application.model.api.PartyNameTypeValues.*;
import static com.ironman.partyreference.application.util.AppUtils.findNameByType;
import static com.ironman.partyreference.application.util.AppUtils.joinNonBlankWith;
import static com.ironman.partyreference.mock.CustomerMock.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;

import com.ironman.partyreference.application.model.api.PartyIdentificationTypeValues;
import com.ironman.partyreference.application.model.api.PartyTypeValues;
import com.ironman.partyreference.application.model.api.ResidencyStatusTypeValues;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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

  @Test
  @DisplayName("Should map person request to CustomerEntity applying name fields")
  void shouldMapPersonRequestToEntity() {
    var request = getRegisterPersonRequest();

    given(partyReferenceTypeResolver.resolveIdentificationTypeCode(any())).willReturn("1");
    given(partyReferenceTypeResolver.resolvePartyTypeCode(any())).willReturn("P");
    given(partyReferenceTypeResolver.resolveResidencyStatusCode(any())).willReturn("N");

    var result = customerMapper.toEntity(request);

    assertNotNull(result);
    assertEquals("1", result.getDocumentType());
    assertEquals("12345678", result.getDocumentNumber());
    assertEquals("P", result.getCustomerType());
    assertEquals("N", result.getResidencyStatus());
    assertEquals("María Elena", result.getName());
    assertEquals("Rodríguez", result.getPaternalSurname());
    assertEquals("Fernández", result.getMaternalSurname());
    assertNull(result.getTradeName());
  }

  @Test
  @DisplayName("Should map organization request to CustomerEntity with trade name and no surnames")
  void shouldMapOrganizationRequestToEntity() {
    var request = getRegisterOrganizationRequest();

    given(partyReferenceTypeResolver.resolveIdentificationTypeCode(any())).willReturn("6");
    given(partyReferenceTypeResolver.resolvePartyTypeCode(any())).willReturn("O");
    given(partyReferenceTypeResolver.resolveResidencyStatusCode(any())).willReturn("N");

    var result = customerMapper.toEntity(request);

    assertNotNull(result);
    assertEquals("6", result.getDocumentType());
    assertEquals("20330791412", result.getDocumentNumber());
    assertEquals("O", result.getCustomerType());
    assertEquals("N", result.getResidencyStatus());
    assertEquals("Saga Falabella S.A.", result.getName());
    assertNull(result.getPaternalSurname());
    assertNull(result.getMaternalSurname());
    assertEquals("Falabella", result.getTradeName());
  }

  @Test
  @DisplayName("Should update CustomerEntity from person request preserving entity id")
  void shouldUpdateEntityFromPersonRequest() {
    var customer = getCustomerTypeOrganization();
    var request = getRegisterPersonRequest();

    given(partyReferenceTypeResolver.resolveIdentificationTypeCode(any())).willReturn("1");
    given(partyReferenceTypeResolver.resolvePartyTypeCode(any())).willReturn("P");
    given(partyReferenceTypeResolver.resolveResidencyStatusCode(any())).willReturn("N");

    customerMapper.updateEntity(customer, request);

    assertEquals(2L, customer.getId());
    assertEquals("1", customer.getDocumentType());
    assertEquals("12345678", customer.getDocumentNumber());
    assertEquals("P", customer.getCustomerType());
    assertEquals("N", customer.getResidencyStatus());
    assertEquals("María Elena", customer.getName());
    assertEquals("Rodríguez", customer.getPaternalSurname());
    assertEquals("Fernández", customer.getMaternalSurname());
    assertNull(customer.getTradeName());
  }

  @Test
  @DisplayName("Should update CustomerEntity from organization request preserving entity id")
  void shouldUpdateEntityFromOrganizationRequest() {
    var customer = getCustomerTypePerson();
    var request = getRegisterOrganizationRequest();

    given(partyReferenceTypeResolver.resolveIdentificationTypeCode(any())).willReturn("6");
    given(partyReferenceTypeResolver.resolvePartyTypeCode(any())).willReturn("O");
    given(partyReferenceTypeResolver.resolveResidencyStatusCode(any())).willReturn("N");

    customerMapper.updateEntity(customer, request);

    assertEquals(1L, customer.getId());
    assertEquals("6", customer.getDocumentType());
    assertEquals("20330791412", customer.getDocumentNumber());
    assertEquals("O", customer.getCustomerType());
    assertEquals("N", customer.getResidencyStatus());
    assertEquals("Saga Falabella S.A.", customer.getName());
    assertNull(customer.getPaternalSurname());
    assertNull(customer.getMaternalSurname());
    assertEquals("Falabella", customer.getTradeName());
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
