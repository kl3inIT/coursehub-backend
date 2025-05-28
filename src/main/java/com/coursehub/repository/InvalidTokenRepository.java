package com.coursehub.repository;

import com.coursehub.entity.InvalidTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InvalidTokenRepository extends JpaRepository<InvalidTokenEntity, String> {

}
