package com.coursehub.repository.impl;

import com.coursehub.dto.request.course.CourseSearchRequestDTO;
import com.coursehub.entity.CourseEntity;
import com.coursehub.enums.CourseLevel;
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
    }

    private List<Predicate> buildSearchPredicates(CriteriaBuilder cb, Root<CourseEntity> root, CourseSearchRequestDTO searchRequest) {
        List<Predicate> predicates = new ArrayList<>();

        // Tìm theo từ khóa (title hoặc description)
        if (StringUtils.hasText(searchRequest.getSearchTerm())) {
            String searchTerm = searchRequest.getSearchTerm().toLowerCase();
            predicates.add(cb.or(
                    cb.like(cb.lower(root.get("title")), "%" + searchTerm + "%"),
                    cb.like(cb.lower(root.get("description")), "%" + searchTerm + "%")
            ));
        }

        // Lọc theo danh mục
        if (searchRequest.getCategoryId() != null) {
            predicates.add(cb.equal(root.get("categoryEntity").get("id"), searchRequest.getCategoryId()));
        }

        // Lọc theo level
        if (StringUtils.hasText(searchRequest.getLevel())) {
            CourseLevel level = CourseLevel.fromString(searchRequest.getLevel());
            predicates.add(cb.equal(root.get("level"), level));
        }

        // Lọc theo khoảng giá
        if (searchRequest.getMinPrice() != null) {
            predicates.add(cb.greaterThanOrEqualTo(root.get("price"), searchRequest.getMinPrice()));
        }
        if (searchRequest.getMaxPrice() != null) {
            predicates.add(cb.lessThanOrEqualTo(root.get("price"), searchRequest.getMaxPrice()));
        }

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
            log.warn("Invalid sortBy field: {}", sortBy);
            // Không áp dụng sort nếu field không hợp lệ
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
