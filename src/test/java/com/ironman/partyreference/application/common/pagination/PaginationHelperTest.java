package com.ironman.partyreference.application.common.pagination;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;

import com.ironman.partyreference.application.model.api.PageQuery;
import com.ironman.partyreference.application.model.api.SortDirectionValues;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Page;
import io.quarkus.panache.common.Sort;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PaginationHelperTest {

  @Mock private PanacheQuery<String> panacheQuery;

  static Stream<Arguments> sortDirectionProvider() {
    return Stream.of(
        Arguments.of("ascending", SortDirectionValues.ASCENDENTE, Sort.Direction.Ascending),
        Arguments.of("descending", SortDirectionValues.DESCENDENTE, Sort.Direction.Descending),
        Arguments.of("null defaults to descending", null, Sort.Direction.Descending));
  }

  @ParameterizedTest(name = "[{index}] {0}")
  @MethodSource("sortDirectionProvider")
  @DisplayName("Should resolve sort direction to the corresponding Panache Sort.Direction")
  void shouldResolveSortDirectionToPanacheDirection(
      String displayName, SortDirectionValues sortDirection, Sort.Direction expectedDirection) {
    assertEquals(expectedDirection, PaginationHelper.resolveDirection(sortDirection));
  }

  static Stream<Arguments> pageQueryProvider() {
    return Stream.of(
        Arguments.of("first page", 1, 10, 0, 10),
        Arguments.of("third page", 3, 5, 2, 5));
  }

  @ParameterizedTest(name = "[{index}] {0}")
  @MethodSource("pageQueryProvider")
  @DisplayName("Should convert 1-based page number to 0-based Panache page offset")
  void shouldConvertPageQueryToPanachePage(
      String displayName,
      int pageNumber,
      int pageSize,
      int expectedPageIndex,
      int expectedPageSize) {
    var query = PageQuery.builder().pageNumber(pageNumber).pageSize(pageSize).build();

    var result = PaginationHelper.toPage(query);

    assertEquals(expectedPageIndex, result.index);
    assertEquals(expectedPageSize, result.size);
  }

  @Test
  @DisplayName("Should map stream results through converter and build correct pagination metadata")
  void shouldMapResultsThroughConverterAndBuildPagination() {
    var items = List.of("alice", "bob");
    var page = Page.of(0, 10);
    given(panacheQuery.stream()).willReturn(items.stream());
    given(panacheQuery.page()).willReturn(page);
    given(panacheQuery.count()).willReturn((long) items.size());
    given(panacheQuery.pageCount()).willReturn(1);

    var result = PaginationHelper.toPaginatedResult(panacheQuery, String::toUpperCase);

    assertNotNull(result);
    assertEquals(items.size(), result.getData().size());
    assertEquals("ALICE", result.getData().get(0));
    assertEquals("BOB", result.getData().get(1));

    var pagination = result.getPagination();
    assertNotNull(pagination);
    assertEquals(page.index + 1, pagination.getCurrentPage());
    assertEquals(page.size, pagination.getPageSize());
    assertEquals((long) items.size(), pagination.getTotalElements());
    assertEquals(1, pagination.getTotalPages());
  }

  @Test
  @DisplayName("Should return empty data and page metadata with 1-based current page when query has no results")
  void shouldReturnEmptyDataWhenQueryHasNoResults() {
    var page = Page.of(1, 5);
    given(panacheQuery.stream()).willReturn(Stream.<String>of());
    given(panacheQuery.page()).willReturn(page);
    given(panacheQuery.count()).willReturn(0L);
    given(panacheQuery.pageCount()).willReturn(0);

    var result = PaginationHelper.toPaginatedResult(panacheQuery, String::toUpperCase);

    assertNotNull(result);
    assertTrue(result.getData().isEmpty());

    var pagination = result.getPagination();
    assertNotNull(pagination);
    assertEquals(page.index + 1, pagination.getCurrentPage());
    assertEquals(page.size, pagination.getPageSize());
    assertEquals(0L, pagination.getTotalElements());
    assertEquals(0, pagination.getTotalPages());
  }
}
