package com.ironman.partyreference.application.util;

import com.ironman.partyreference.application.model.api.DirectoryEntryDate;
import com.ironman.partyreference.application.model.api.DirectoryEntryDateTypeValues;
import com.ironman.partyreference.application.model.api.PartyName;
import com.ironman.partyreference.application.model.api.PartyNameTypeValues;
import java.time.LocalDateTime;
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

  public static boolean isBlank(String value) {
    return value == null || value.isBlank();
  }
}
