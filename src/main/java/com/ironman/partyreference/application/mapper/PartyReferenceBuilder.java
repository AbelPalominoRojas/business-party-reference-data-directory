package com.ironman.partyreference.application.mapper;

import static com.ironman.partyreference.application.model.api.PartyNameTypeValues.*;
import static com.ironman.partyreference.application.model.api.PartyNameTypeValues.NOMBRE_COMPLETO;
import static com.ironman.partyreference.application.util.AppUtils.buildPartyName;

import com.ironman.partyreference.application.model.api.PartyName;
import com.ironman.partyreference.application.model.entity.CustomerEntity;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PartyReferenceBuilder {
  public static List<PartyName> buildNaturalPersonNames(CustomerEntity customer) {
    var fullName =
        Stream.of(customer.getName(), customer.getPaternalSurname(), customer.getMaternalSurname())
            .filter(s -> s != null && !s.isBlank())
            .collect(Collectors.joining(" "));

    return List.of(
        buildPartyName(customer.getName(), NOMBRE),
        buildPartyName(customer.getPaternalSurname(), APELLIDO_PATERNO),
        buildPartyName(customer.getMaternalSurname(), APELLIDO_MATERNO),
        buildPartyName(fullName, NOMBRE_COMPLETO));
  }

  public static List<PartyName> buildOrganizationNames(CustomerEntity customer) {
    return List.of(
        buildPartyName(customer.getName(), RAZON_SOCIAL),
        buildPartyName(customer.getTradeName(), NOMBRE_FANTASIA));
  }
}
