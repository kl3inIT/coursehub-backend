package com.coursehub.repository.impl;

import com.coursehub.dto.request.course.CourseSearchRequestDTO;
import com.coursehub.entity.CourseEntity;
import com.coursehub.enums.CourseLevel;
import com.coursehub.enums.CourseStatus;
import com.coursehub.exceptions.course.InvalidSearchParametersException;
import com.coursehub.exceptions.course.SearchOperationException;
import com.coursehub.repository.SearchRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import static com.coursehub.constant.Constant.SearchConstants.*;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Repository
@RequiredArgsConstructor
public class SearchRepositoryImpl implements SearchRepository {

    private final EntityManager entityManager;

    @Override
    public Page<CourseEntity> advancedSearch(CourseSearchRequestDTO searchRequest, Pageable pageable) {
        log.debug("Performing search with criteria: {}", searchRequest);

        try {
            CriteriaBuilder cb = entityManager.getCriteriaBuilder();
            CriteriaQuery<CourseEntity> query = cb.createQuery(CourseEntity.class);
            Root<CourseEntity> root = query.from(CourseEntity.class);

            List<Predicate> predicates = buildSearchPredicates(cb, root, searchRequest);
            query.where(predicates.toArray(new Predicate[0]));

            // Add sorting
            addSorting(cb, query, root, searchRequest.getSortBy(), searchRequest.getSortDirection());

            Long total = getTotalCount(cb, searchRequest);

            // Execute main query with pagination
            List<CourseEntity> results = executeQuery(query, pageable);

            return new PageImpl<>(results, pageable, total);

        } catch (Exception ex) {
            log.error("Error performing advanced search: {}", ex.getMessage(), ex);
            throw new SearchOperationException("Failed to execute search query", ex);
        }
    }

    private List<Predicate> buildSearchPredicates(CriteriaBuilder cb, Root<CourseEntity> root, CourseSearchRequestDTO searchRequest) {
        List<Predicate> predicates = new ArrayList<>();

        if (StringUtils.hasText(searchRequest.getSearchTerm())) {
            String searchTerm = searchRequest.getSearchTerm().toLowerCase();
            predicates.add(cb.or(
                    cb.like(cb.lower(root.get("title")), "%" + searchTerm + "%"),
                    cb.like(cb.lower(root.get("description")), "%" + searchTerm + "%")
            ));
        }

        // Lọc theo nhiều danh mục
        if (searchRequest.getCategoryIds() != null && !searchRequest.getCategoryIds().isEmpty()) {
            predicates.add(root.get("categoryEntity").get("id").in(searchRequest.getCategoryIds()));
        }

        // Lọc theo nhiều level
        if (searchRequest.getLevels() != null && !searchRequest.getLevels().isEmpty()) {
            List<CourseLevel> levelEnums = searchRequest.getLevels().stream()
                .map(CourseLevel::fromString)
                .toList();
            predicates.add(root.get("level").in(levelEnums));
        }

        // Lọc theo free courses
        if (searchRequest.getIsFree() != null && searchRequest.getIsFree()) {
            predicates.add(cb.equal(root.get("price"), 0.0));
        } else {
            // Lọc theo khoảng giá (chỉ khi không phải free)
            if (searchRequest.getMinPrice() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("price"), searchRequest.getMinPrice()));
            }
            if (searchRequest.getMaxPrice() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("price"), searchRequest.getMaxPrice()));
            }
        }

        // Lọc theo discounted courses
        if (searchRequest.getIsDiscounted() != null && searchRequest.getIsDiscounted()) {
            predicates.add(cb.and(
                    cb.isNotNull(root.get("discount")),
                    cb.greaterThan(root.get("discount"), 0.0)
            ));
        }

        predicates.add(cb.equal(root.get("status"), CourseStatus.PUBLISHED));

        return predicates;
    }

    private void addSorting(CriteriaBuilder cb, CriteriaQuery<CourseEntity> query, Root<CourseEntity> root,
                            String sortBy, String sortDirection) {
        // Thiết lập mặc định nếu thiếu
        if (!StringUtils.hasText(sortBy)) {
            sortBy = DEFAULT_SORT_BY;
        }
        if (!StringUtils.hasText(sortDirection)) {
            sortDirection = DEFAULT_SORT_DIRECTION;
        }

        try {
            Path<Object> sortPath = root.get(sortBy);
            if (SORT_DESC.equalsIgnoreCase(sortDirection)) {
                query.orderBy(cb.desc(sortPath));
            } else {
                query.orderBy(cb.asc(sortPath));
            }
        } catch (IllegalArgumentException e) {
            log.error("Invalid sortBy field: {}", sortBy);
            throw new InvalidSearchParametersException("Invalid sort field: " + sortBy, e);
        }
    }

    private Long getTotalCount(CriteriaBuilder cb, CourseSearchRequestDTO searchRequest) {
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<CourseEntity> countRoot = countQuery.from(CourseEntity.class);

        List<Predicate> countPredicates = buildSearchPredicates(cb, countRoot, searchRequest);
        countQuery.select(cb.count(countRoot)).where(countPredicates.toArray(new Predicate[0]));
        return entityManager.createQuery(countQuery).getSingleResult();
    }

    private List<CourseEntity> executeQuery(CriteriaQuery<CourseEntity> query, Pageable pageable) {
        TypedQuery<CourseEntity> typedQuery = entityManager.createQuery(query);
        typedQuery.setFirstResult((int) pageable.getOffset());
        typedQuery.setMaxResults(pageable.getPageSize());
        return typedQuery.getResultList();
    }
}
