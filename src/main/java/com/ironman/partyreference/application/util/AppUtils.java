package com.ironman.partyreference.application.util;

import static com.ironman.partyreference.application.model.api.PartyNameTypeValues.NOMBRE;
import static com.ironman.partyreference.application.model.api.PartyNameTypeValues.RAZON_SOCIAL;
import static com.ironman.partyreference.application.model.api.PartyTypeValues.PERSONA;

import com.ironman.partyreference.application.model.api.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AppUtils {

  public static PartyName buildPartyName(String nameValue, PartyNameTypeValues nameType) {
    return new PartyName().partyName(nameValue).partyNameType(nameType);
  }

  public static PartyNameTypeValues resolvePrimaryNameType(PartyTypeValues partyType) {
    return PERSONA == partyType ? NOMBRE : RAZON_SOCIAL;
  }

  public static DirectoryEntryDate buildDirectoryEntryDate(
      LocalDateTime dateTime, DirectoryEntryDateTypeValues entryDateType) {
    return new DirectoryEntryDate()
        .directoryEntryDate(dateTime)
        .directoryEntryDateType(entryDateType);
  }

  public static String findNameByType(
      List<PartyName> partyNames, PartyNameTypeValues partyNameType) {
    return partyNames.stream()
        .filter(name -> name.getPartyNameType() == partyNameType)
        .findFirst()
        .map(PartyName::getPartyName)
        .orElse(null);
  }

  public static boolean isBlank(String value) {
    return value == null || value.isBlank();
  }

  public static String joinNonBlankWith(String delimiter, String... parts) {
    return Stream.of(parts).filter(part -> !isBlank(part)).collect(Collectors.joining(delimiter));
  }
}
