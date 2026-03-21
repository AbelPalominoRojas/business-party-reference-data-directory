package com.ironman.partyreference.application.model.api;

import java.io.Serializable;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PaginatedResult<T> implements Serializable {
  private List<T> data;
  private Pagination pagination;
}
