package com.ironman.partyreference.application.common.pagination;

import com.ironman.partyreference.application.model.api.PageQuery;
import com.ironman.partyreference.application.model.api.PaginatedResult;
import com.ironman.partyreference.application.model.api.Pagination;
import com.ironman.partyreference.application.model.api.SortDirectionValues;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Page;
import io.quarkus.panache.common.Sort;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PaginationHelper {

  public static <T, U> PaginatedResult<U> toPaginatedResult(
      PanacheQuery<T> query, Function<T, U> converter) {

    List<U> data = query.stream().map(converter).toList();

    Pagination pagination = toPagination(query);

    return PaginatedResult.<U>builder().data(data).pagination(pagination).build();
  }

  public static Page toPage(PageQuery query) {
    return Page.of(query.getPageNumber() - 1, query.getPageSize());
  }

  public static Sort.Direction resolveDirection(SortDirectionValues sortDirection) {
    return Optional.ofNullable(sortDirection)
        .filter(order -> order == SortDirectionValues.ASCENDENTE)
        .map(order -> Sort.Direction.Ascending)
        .orElse(Sort.Direction.Descending);
  }

  private static <T> Pagination toPagination(PanacheQuery<T> query) {
    return new Pagination()
        .currentPage(query.page().index + 1)
        .pageSize(query.page().size)
        .totalElements(query.count())
        .totalPages(query.pageCount());
  }
}
