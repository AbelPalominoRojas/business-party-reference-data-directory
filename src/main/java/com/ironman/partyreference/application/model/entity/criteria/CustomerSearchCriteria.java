package com.ironman.partyreference.application.model.entity.criteria;

import io.quarkus.panache.common.Page;
import io.quarkus.panache.common.Sort;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerSearchCriteria implements Serializable {
  private String documentNumber;
  private String customerType;
  private String residencyStatus;
  private Page page;
  private Sort sort;
}
