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

  public PartyIdentificationTypeValues resolveIdentificationType(String documentTypeCode) {
    var displayName = resolveDisplayName(documentTypeCode, properties.identificationTypes());
    return resolveEnumByDisplayName(displayName, PartyIdentificationTypeValues.values());
  }

  public PartyTypeValues resolvePartyType(String partyTypeCode) {
    var displayName = resolveDisplayName(partyTypeCode, properties.partyTypes());
    return resolveEnumByDisplayName(displayName, PartyTypeValues.values());
  }

  public ResidencyStatusTypeValues resolveResidencyStatus(String residencyStatusCode) {
    var displayName = resolveDisplayName(residencyStatusCode, properties.residencyStatusTypes());
    return resolveEnumByDisplayName(displayName, ResidencyStatusTypeValues.values());
  }

  private String resolveDisplayName(String sourceCode, List<PartyReferenceType> configuredTypes) {
    if (isBlank(sourceCode)) {
      return null;
    }
    return configuredTypes.stream()
        .filter(configuredType -> sourceCode.equalsIgnoreCase(configuredType.getCode()))
        .findFirst()
        .map(PartyReferenceType::getName)
        .orElse(null);
  }

  private <E extends Enum<E>> E resolveEnumByDisplayName(String displayName, E[] enumValues) {
    if (isBlank(displayName)) {
      return null;
    }

    return Arrays.stream(enumValues)
        .filter(value -> displayName.equalsIgnoreCase(value.toString()))
        .findFirst()
        .orElse(null);
  }
}
