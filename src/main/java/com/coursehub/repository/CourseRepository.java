package com.coursehub.repository;

import com.coursehub.entity.CourseEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.lang.NonNull;
import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository<CourseEntity, Long> {

    @Query("SELECT c FROM CourseEntity c LEFT JOIN c.enrollmentEntities e GROUP BY c.id ORDER BY COUNT(e.id) DESC")
    List<CourseEntity> findFeaturedCourse(Pageable pageable);

    @NonNull
    Page<CourseEntity> findAll(Pageable pageable);

    List<CourseEntity> findByCategoryEntity_Id(Long categoryId);

    @Query("SELECT c FROM CourseEntity c WHERE " +
            "(:search IS NULL OR LOWER(c.title) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(c.description) LIKE LOWER(CONCAT('%', :search, '%'))) AND " +
            "(:categoryId IS NULL OR c.categoryEntity.id = :categoryId) AND " +
            "(:level IS NULL OR c.level = :level) AND " +
            "(:minPrice IS NULL OR COALESCE(c.price - c.discount, c.price) >= :minPrice) AND " +
            "(:maxPrice IS NULL OR COALESCE(c.price - c.discount, c.price) <= :maxPrice)")
    Page<CourseEntity> searchCourses(@Param("search") String search,
                                   @Param("categoryId") Long categoryId,
                                   @Param("level") String level,
                                   @Param("minPrice") Double minPrice,
                                   @Param("maxPrice") Double maxPrice,
                                   Pageable pageable);

}
