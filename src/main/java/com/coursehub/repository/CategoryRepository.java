package com.coursehub.repository;

import com.coursehub.entity.CategoryEntity;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<CategoryEntity, Long> {

    @Query("SELECT c FROM CategoryEntity c WHERE (:name IS NULL OR LOWER(c.name) LIKE LOWER(CONCAT('%', :name, '%')))")
    Page<CategoryEntity> findAll(@Param("name") String name, Pageable pageable);

    @Query("SELECT c.name, COUNT(course) FROM CategoryEntity c LEFT JOIN c.courseEntities course GROUP BY c.id, c.name")
    List<Object[]> getCategoryCourseCounts();

    @Query("SELECT c.id, c.name, c.description, COUNT(course), AVG(r.star), COUNT(DISTINCT e.userEntity.id), SUM(p.amount), c.createdDate, c.modifiedDate " +
           "FROM CategoryEntity c " +
           "LEFT JOIN c.courseEntities course " +
           "LEFT JOIN course.reviewEntities r " +
           "LEFT JOIN course.enrollmentEntities e " +
           "LEFT JOIN course.paymentEntities p " +
           "WHERE c.id = :categoryId " +
           "GROUP BY c.id, c.name, c.description, c.createdDate, c.modifiedDate")
    List<Object[]> getCategoryDetail(@Param("categoryId") Long categoryId);

}
