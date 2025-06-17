package com.coursehub.service.impl;

import com.coursehub.repository.CategoryRepository;
import com.coursehub.service.AnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AnalyticsServiceImpl implements AnalyticsService {
    private final CategoryRepository categoryRepository;

} 