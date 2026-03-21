package com.ironman.partyreference.application.repository;

import static com.ironman.partyreference.application.util.AppUtils.isBlank;

import com.ironman.partyreference.application.model.entity.CustomerEntity;
import com.ironman.partyreference.application.model.entity.criteria.CustomerSearchCriteria;
import com.ironman.partyreference.application.model.entity.projection.CustomerSummaryProjection;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ApplicationScoped
public class CustomerRepository implements PanacheRepositoryBase<CustomerEntity, Long> {

  public PanacheQuery<CustomerSummaryProjection> searchCustomers(CustomerSearchCriteria criteria) {
    Map<String, Object> params = new HashMap<>();
    List<String> predicates = new ArrayList<>();

    if (!isBlank(criteria.getDocumentNumber())) {
      predicates.add("UPPER(documentNumber) LIKE :documentNumber");
      params.put("documentNumber", "%" + criteria.getDocumentNumber() + "%");
    }

    if (!isBlank(criteria.getCustomerType())) {
      predicates.add("customerType = :customerType");
      params.put("customerType", criteria.getCustomerType());
    }

    if (!isBlank(criteria.getResidencyStatus())) {
      predicates.add("residencyStatus = :residencyStatus");
      params.put("residencyStatus", criteria.getResidencyStatus());
    }

    String whereClause = String.join(" AND ", predicates);

    return find(whereClause, criteria.getSort(), params)
        .page(criteria.getPage())
        .project(CustomerSummaryProjection.class);
  }
}
