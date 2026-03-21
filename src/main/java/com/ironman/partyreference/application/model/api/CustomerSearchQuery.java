package com.ironman.partyreference.application.model.api;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class CustomerSearchQuery extends PageQuery implements Serializable {
  private String identifierValue;
  private PartyTypeValues partyType;
  private ResidencyStatusTypeValues residencyStatus;
  private PartyReferenceSortFieldValues sortField;
  private SortDirectionValues sortDirection;
}
