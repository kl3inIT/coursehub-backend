package com.coursehub.repository;

import com.coursehub.dto.request.course.CourseSearchRequestDTO;
import com.coursehub.entity.CourseEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;


public interface SearchRepository {

    Page<CourseEntity> advancedSearch(CourseSearchRequestDTO searchRequest, Pageable pageable);
}
