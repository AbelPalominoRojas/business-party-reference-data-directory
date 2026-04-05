package com.ironman.partyreference.application.mapper;

import static com.ironman.partyreference.application.model.api.PartyIdentificationTypeValues.DOCUMENTO_NACIONAL_IDENTIDAD;
import static com.ironman.partyreference.application.model.api.PartyIdentificationTypeValues.REGISTRO_UNICO_CONTRIBUYENTE;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;

import com.ironman.partyreference.application.config.PartyReferenceProperties;
import com.ironman.partyreference.application.config.PartyReferenceProperties.PartyReferenceType;
import com.ironman.partyreference.application.model.api.PartyTypeValues;
import com.ironman.partyreference.application.model.api.ResidencyStatusTypeValues;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PartyReferenceTypeResolverTest {

  @Mock private PartyReferenceProperties properties;

  @InjectMocks private PartyReferenceTypeResolver resolver;

  @Test
  @DisplayName("Should resolve identification type enum from configured code")
  void shouldResolveIdentificationTypeFromCode() {
    var documentTypes =
        List.of(type("1", "DocumentoNacionalIdentidad"), type("6", "RegistroUnicoContribuyente"));
    given(properties.identificationTypes()).willReturn(documentTypes);

    assertEquals(DOCUMENTO_NACIONAL_IDENTIDAD, resolver.resolveIdentificationType("1"));
  }

  @Test
  @DisplayName("Should resolve party type enum from configured code")
  void shouldResolvePartyTypeFromCode() {
    given(properties.partyTypes())
        .willReturn(List.of(type("P", "Persona"), type("O", "Organizacion")));

    assertEquals(PartyTypeValues.PERSONA, resolver.resolvePartyType("P"));
  }

  @Test
  @DisplayName("Should resolve residency status enum from configured code")
  void shouldResolveResidencyStatusFromCode() {
    given(properties.residencyStatusTypes())
        .willReturn(List.of(type("N", "Nacional"), type("E", "Extranjero")));

    assertEquals(ResidencyStatusTypeValues.NACIONAL, resolver.resolveResidencyStatus("N"));
  }

  @ParameterizedTest(name = "[{index}] code = \"{0}\"")
  @NullSource
  @ValueSource(strings = {"", "  "})
  @DisplayName("Should return null when code is blank or null")
  void shouldReturnNullWhenCodeIsBlankOrNull(String code) {
    assertNull(resolver.resolveIdentificationType(code));
  }

  @Test
  @DisplayName("Should return null when code is not present in configuration")
  void shouldReturnNullWhenCodeIsNotConfigured() {
    given(properties.partyTypes()).willReturn(List.of(type("P", "Persona")));

    assertNull(resolver.resolvePartyType("X"));
  }

  @Test
  @DisplayName("Should resolve identification type code from enum value")
  void shouldResolveIdentificationTypeCode() {
    var documentTypes =
        List.of(type("1", "DocumentoNacionalIdentidad"), type("6", "RegistroUnicoContribuyente"));
    given(properties.identificationTypes()).willReturn(documentTypes);

    assertEquals("6", resolver.resolveIdentificationTypeCode(REGISTRO_UNICO_CONTRIBUYENTE));
  }

  @Test
  @DisplayName("Should resolve party type code from enum value")
  void shouldResolvePartyTypeCode() {
    given(properties.partyTypes())
        .willReturn(List.of(type("P", "Persona"), type("O", "Organizacion")));

    assertEquals("O", resolver.resolvePartyTypeCode(PartyTypeValues.ORGANIZACION));
  }

  @Test
  @DisplayName("Should resolve residency status code from enum value")
  void shouldResolveResidencyStatusCode() {
    given(properties.residencyStatusTypes())
        .willReturn(List.of(type("N", "Nacional"), type("E", "Extranjero")));

    assertEquals("E", resolver.resolveResidencyStatusCode(ResidencyStatusTypeValues.EXTRANJERO));
  }

  @Test
  @DisplayName("Should return null when party type enum value is null")
  void shouldReturnNullWhenPartyTypeEnumValueIsNull() {
    assertNull(resolver.resolvePartyTypeCode(null));
  }

  @Test
  @DisplayName("Should return null when residency status enum value is null")
  void shouldReturnNullWhenResidencyStatusEnumValueIsNull() {
    assertNull(resolver.resolveResidencyStatusCode(null));
  }

  @Test
  @DisplayName("Should return null when identification type enum value is null")
  void shouldReturnNullWhenIdentificationTypeEnumValueIsNull() {
    assertNull(resolver.resolveIdentificationTypeCode(null));
  }

  @Test
  @DisplayName("Should return null when enum value is not present in configuration")
  void shouldReturnNullWhenEnumValueIsNotConfigured() {
    given(properties.residencyStatusTypes()).willReturn(List.of(type("N", "Nacional")));

    assertNull(resolver.resolveResidencyStatusCode(ResidencyStatusTypeValues.EXTRANJERO));
  }

  private static PartyReferenceType type(String code, String name) {
    return new PartyReferenceType() {
      public String getCode() {
        return code;
      }

      public String getName() {
        return name;
      }
    };
  }
}
