package com.ironman.partyreference.application.util;

import com.ironman.partyreference.application.model.api.*;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AppUtils {

  public static PartyName buildPartyName(String nameValue, PartyNameTypeValues nameType) {
    return new PartyName().partyName(nameValue).partyNameType(nameType);
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
}
