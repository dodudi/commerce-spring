package com.commerce.repository;

import com.commerce.domain.Order;
import com.commerce.dto.OrderFilterRequest;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class OrderSpecification {

    private OrderSpecification() {}

    public static Specification<Order> withFilters(OrderFilterRequest filter) {
        return (root, query, cb) -> {
            // count 쿼리에서는 fetch join 제외 (MultipleBagFetchException 방지)
            if (!Long.class.equals(query.getResultType())) {
                root.fetch("member", JoinType.LEFT);
                root.fetch("orderItems", JoinType.LEFT);
                query.distinct(true);
            }

            List<Predicate> predicates = new ArrayList<>();

            if (filter.getMemberId() != null) {
                predicates.add(cb.equal(root.get("member").get("id"), filter.getMemberId()));
            }
            if (filter.getStatus() != null) {
                predicates.add(cb.equal(root.get("status"), filter.getStatus()));
            }
            if (filter.getStartDate() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("createdAt"), filter.getStartDate()));
            }
            if (filter.getEndDate() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("createdAt"), filter.getEndDate()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
