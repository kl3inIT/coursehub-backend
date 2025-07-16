package com.coursehub.repository;

import com.coursehub.entity.FeedbackEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FeedbackRepository extends JpaRepository<FeedbackEntity, Long> {

    List<FeedbackEntity> findByUserEntity_Id(Long id);

    List<FeedbackEntity> findAllByOrderByCreatedDateDesc();

    FeedbackEntity findById(long id);

}
