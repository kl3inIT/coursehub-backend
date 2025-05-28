package com.coursehub.repository;

import com.coursehub.entity.CourseEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface CourseRepository extends JpaRepository<CourseEntity, Long> {

}
