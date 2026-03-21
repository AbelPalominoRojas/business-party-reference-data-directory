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
public class PageQuery implements Serializable {
  private Integer pageNumber;
  private Integer pageSize;
}
