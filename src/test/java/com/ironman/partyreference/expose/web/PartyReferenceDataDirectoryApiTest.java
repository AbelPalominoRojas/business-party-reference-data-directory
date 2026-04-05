package com.ironman.partyreference.expose.web;

import static com.ironman.partyreference.application.model.api.PartyIdentificationTypeValues.DOCUMENTO_NACIONAL_IDENTIDAD;
import static com.ironman.partyreference.application.model.api.PartyIdentificationTypeValues.REGISTRO_UNICO_CONTRIBUYENTE;
import static com.ironman.partyreference.application.model.api.PartyTypeValues.ORGANIZACION;
import static com.ironman.partyreference.application.model.api.PartyTypeValues.PERSONA;
import static com.ironman.partyreference.mock.CustomerMock.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;

import com.ironman.partyreference.application.model.api.PartyIdentificationTypeValues;
import com.ironman.partyreference.application.model.api.PartyTypeValues;
import com.ironman.partyreference.application.model.api.ResidencyStatusTypeValues;
import com.ironman.partyreference.application.model.entity.CustomerEntity;
import com.ironman.partyreference.application.repository.CustomerRepository;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

@QuarkusTest
class PartyReferenceDataDirectoryApiTest {

  private static final long PARTY_ENTRY_ID = 1L;
  private static final String RETRIEVE_ENDPOINT =
      "/party-reference-data-directory/{partyReferenceDataDirectoryEntryId}/retrieve";
  private static final Map<String, String> REQUEST_HEADERS =
      Map.of("Request-ID", "bb83da56-6bb4-4cf6-a10f-f10b1104084f");

  @InjectMock private CustomerRepository customerRepository;

  static Stream<Arguments> customerByTypeProvider() {
    return Stream.of(
        Arguments.of("person", getCustomerTypePerson(), DOCUMENTO_NACIONAL_IDENTIDAD, PERSONA, 4),
        Arguments.of(
            "organization",
            getCustomerTypeOrganization(),
            REGISTRO_UNICO_CONTRIBUYENTE,
            ORGANIZACION,
            2));
  }

  @ParameterizedTest(name = "[{index}] {0}")
  @MethodSource("customerByTypeProvider")
  @DisplayName("Should return 200 with party reference data when customer is found by type")
  void shouldRetrievePartyReferenceWhenCustomerExistsByType(
      String displayName,
      CustomerEntity customer,
      PartyIdentificationTypeValues expectedIdentificationType,
      PartyTypeValues expectedPartyType,
      int expectedPartyNamesSize) {

    given(customerRepository.findByIdOptional(anyLong())).willReturn(Optional.of(customer));

    RestAssured.given()
        .headers(REQUEST_HEADERS)
        .contentType(ContentType.JSON)
        .when()
        .get(RETRIEVE_ENDPOINT, PARTY_ENTRY_ID)
        .then()
        .statusCode(200)
        .body("partyReference.partyId", Matchers.notNullValue())
        .body(
            "partyReference.partyIdentification.partyIdentificationType",
            Matchers.equalTo(expectedIdentificationType.toString()))
        .body(
            "partyReference.partyIdentification.partyIdentification.identifierValue",
            Matchers.equalTo(customer.getDocumentNumber()))
        .body("partyReference.partyNames.size()", Matchers.is(expectedPartyNamesSize))
        .body("partyType", Matchers.equalTo(expectedPartyType.toString()))
        .body("residencyStatus", Matchers.equalTo(ResidencyStatusTypeValues.NACIONAL.toString()));
  }

  @Test
  @DisplayName("Should return 204 when customer is not found")
  void shouldReturnNoContentWhenCustomerNotFound() {
    given(customerRepository.findByIdOptional(anyLong())).willReturn(Optional.empty());

    RestAssured.given()
        .headers(REQUEST_HEADERS)
        .contentType(ContentType.JSON)
        .when()
        .get(RETRIEVE_ENDPOINT, PARTY_ENTRY_ID)
        .then()
        .statusCode(204);
  }
}
