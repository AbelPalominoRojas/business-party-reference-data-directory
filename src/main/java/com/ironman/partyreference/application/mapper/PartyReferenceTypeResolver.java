package com.ironman.partyreference.application.mapper;

import static com.ironman.partyreference.application.config.PartyReferenceProperties.PartyReferenceType;
import static com.ironman.partyreference.application.util.AppUtils.isBlank;

import com.ironman.partyreference.application.config.PartyReferenceProperties;
import com.ironman.partyreference.application.model.api.PartyIdentificationTypeValues;
import com.ironman.partyreference.application.model.api.PartyTypeValues;
import com.ironman.partyreference.application.model.api.ResidencyStatusTypeValues;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@ApplicationScoped
public class PartyReferenceTypeResolver {
  private final PartyReferenceProperties properties;

  public PartyIdentificationTypeValues resolveIdentificationType(String identificationTypeCode) {
    var enumName = findConfiguredName(identificationTypeCode, properties.identificationTypes());
    return findEnumByName(enumName, PartyIdentificationTypeValues.values());
  }

  public PartyTypeValues resolvePartyType(String partyTypeCode) {
    var enumName = findConfiguredName(partyTypeCode, properties.partyTypes());
    return findEnumByName(enumName, PartyTypeValues.values());
  }

  public ResidencyStatusTypeValues resolveResidencyStatus(String residencyStatusCode) {
    var enumName = findConfiguredName(residencyStatusCode, properties.residencyStatusTypes());
    return findEnumByName(enumName, ResidencyStatusTypeValues.values());
  }

  public String resolvePartyTypeCode(PartyTypeValues partyType) {
    return resolveCode(partyType, properties.partyTypes());
  }

  public String resolveResidencyStatusCode(ResidencyStatusTypeValues residencyStatus) {
    return resolveCode(residencyStatus, properties.residencyStatusTypes());
  }

  private String findConfiguredName(String code, List<PartyReferenceType> configuredTypes) {
    if (isBlank(code)) {
      return null;
    }
    return configuredTypes.stream()
        .filter(type -> code.equalsIgnoreCase(type.getCode()))
        .findFirst()
        .map(PartyReferenceType::getName)
        .orElse(null);
  }

  private <E extends Enum<E>> E findEnumByName(String name, E[] enumValues) {
    if (isBlank(name)) {
      return null;
    }
    return Arrays.stream(enumValues)
        .filter(value -> name.equalsIgnoreCase(value.toString()))
        .findFirst()
        .orElse(null);
  }

  private <E extends Enum<E>> String resolveCode(
      E enumValue, List<PartyReferenceType> configuredTypes) {
    if (enumValue == null) {
      return null;
    }
    var enumName = enumValue.toString();
    return configuredTypes.stream()
        .filter(type -> type.getName().equalsIgnoreCase(enumName))
        .findFirst()
        .map(PartyReferenceType::getCode)
        .orElse(null);
  }
}
