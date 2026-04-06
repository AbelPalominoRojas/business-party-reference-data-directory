package com.ironman.partyreference.expose.web;

import static com.ironman.partyreference.application.model.api.PartyIdentificationTypeValues.DOCUMENTO_NACIONAL_IDENTIDAD;
import static com.ironman.partyreference.application.model.api.PartyIdentificationTypeValues.REGISTRO_UNICO_CONTRIBUYENTE;
import static com.ironman.partyreference.application.model.api.PartyTypeValues.ORGANIZACION;
import static com.ironman.partyreference.application.model.api.PartyTypeValues.PERSONA;
import static com.ironman.partyreference.mock.CustomerMock.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

import com.ironman.partyreference.application.model.api.PartyIdentificationTypeValues;
import com.ironman.partyreference.application.model.api.PartyTypeValues;
import com.ironman.partyreference.application.model.api.RegisterPartyReferenceDataDirectoryEntryRequest;
import com.ironman.partyreference.application.model.api.ResidencyStatusTypeValues;
import com.ironman.partyreference.application.model.entity.CustomerEntity;
import com.ironman.partyreference.application.model.entity.criteria.CustomerSearchCriteria;
import com.ironman.partyreference.application.model.entity.projection.CustomerSummaryProjection;
import com.ironman.partyreference.application.repository.CustomerRepository;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Page;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;

@QuarkusTest
class PartyReferenceDataDirectoryApiTest {

  private static final long PARTY_ENTRY_ID = 1L;
  private static final String REGISTER_ENDPOINT = "/party-reference-data-directory/register";
  private static final String SEARCH_ENDPOINT = "/party-reference-data-directory/retrieve";
  private static final String RETRIEVE_ENDPOINT =
      "/party-reference-data-directory/{partyReferenceDataDirectoryEntryId}/retrieve";
  private static final String UPDATE_ENDPOINT =
      "/party-reference-data-directory/{partyReferenceDataDirectoryEntryId}/update";
  private static final Map<String, String> REQUEST_HEADERS =
      Map.of("Request-ID", "bb83da56-6bb4-4cf6-a10f-f10b1104084f");

  @InjectMock private CustomerRepository customerRepository;

  // @InjectMock PanacheQuery<CustomerSummaryProjection> panacheQuery;

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

  static Stream<Arguments> registerRequestByTypeProvider() {
    return Stream.of(
        Arguments.of("person", getRegisterPersonRequest()),
        Arguments.of("organization", getRegisterOrganizationRequest()));
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

  @ParameterizedTest(name = "[{index}] {0}")
  @MethodSource("registerRequestByTypeProvider")
  @DisplayName("Should return 201 when party reference entry is registered")
  void shouldRegisterPartyReferenceEntryAndReturn201(
      String displayName, RegisterPartyReferenceDataDirectoryEntryRequest request) {

    given(customerRepository.findByDocumentTypeAndNumber(anyString(), anyString()))
        .willReturn(Optional.empty());

    RestAssured.given()
        .headers(REQUEST_HEADERS)
        .contentType(ContentType.JSON)
        .body(request)
        .when()
        .post(REGISTER_ENDPOINT)
        .then()
        .statusCode(201)
        .body("partyReference", Matchers.notNullValue());
  }

  @Test
  @DisplayName("Should return 200 with paginated results when searching party reference entries")
  @SuppressWarnings("unchecked")
  void shouldReturnPaginatedResultsWhenSearchingPartyReferenceEntries() {
    List<CustomerSummaryProjection> customers = getCustomerSummary();
    PanacheQuery<CustomerSummaryProjection> panacheQuery = Mockito.mock(PanacheQuery.class);
    given(panacheQuery.stream()).willReturn(customers.stream());
    given(panacheQuery.page()).willReturn(Page.of(0, 10));
    given(panacheQuery.count()).willReturn((long) customers.size());
    given(panacheQuery.pageCount()).willReturn(1);
    given(customerRepository.searchCustomers(any(CustomerSearchCriteria.class)))
        .willReturn(panacheQuery);

    RestAssured.given()
        .headers(REQUEST_HEADERS)
        .contentType(ContentType.JSON)
        .queryParam("pageNumber", 1)
        .queryParam("pageSize", 10)
        .when()
        .get(SEARCH_ENDPOINT)
        .then()
        .statusCode(200)
        .body("data", Matchers.notNullValue())
        .body("data.size()", Matchers.is(customers.size()))
        .body("pagination", Matchers.notNullValue());
  }

  @ParameterizedTest(name = "[{index}] {0}")
  @MethodSource("registerRequestByTypeProvider")
  @DisplayName("Should return 200 when party reference entry is updated")
  void shouldUpdatePartyReferenceEntryAndReturn200(
      String displayName, RegisterPartyReferenceDataDirectoryEntryRequest request) {

    given(customerRepository.findByIdOptional(anyLong()))
        .willReturn(Optional.of(getCustomerTypePerson()));
    given(customerRepository.findByDocumentTypeAndNumber(anyString(), anyString()))
        .willReturn(Optional.empty());

    RestAssured.given()
        .headers(REQUEST_HEADERS)
        .contentType(ContentType.JSON)
        .body(request)
        .when()
        .put(UPDATE_ENDPOINT, PARTY_ENTRY_ID)
        .then()
        .statusCode(200)
        .body("partyReference", Matchers.notNullValue());
  }

  @Test
  @DisplayName("Should return 400 when Request-ID header is missing")
  void shouldReturn400WhenRequestIdHeaderIsMissing() {
    RestAssured.given()
        .contentType(ContentType.JSON)
        .when()
        .get(RETRIEVE_ENDPOINT, PARTY_ENTRY_ID)
        .then()
        .statusCode(400);
  }
}
