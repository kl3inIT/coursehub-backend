package com.coursehub.controller;

import com.coursehub.dto.ResponseGeneral;
import com.coursehub.dto.response.analytics.CategoryAnalyticsDetailResponseDTO;
import com.coursehub.dto.response.analytics.CourseAnalyticsDetailResponseDTO;
import com.coursehub.exceptions.analytics.AnalyticsRetrievalException;
import com.coursehub.service.AnalyticsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Calendar;
import java.util.Date;

import static com.coursehub.constant.Constant.CommonConstants.SUCCESS;

@RestController
@RequestMapping("/api/analytics")
@RequiredArgsConstructor
@Slf4j
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    @GetMapping("/categories/details")
    public ResponseEntity<ResponseGeneral<Page<CategoryAnalyticsDetailResponseDTO>>> getCategoryAnalyticsDetails(
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate,
            @RequestParam(required = false) String range,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id,asc") String[] sort
    ) throws AnalyticsRetrievalException {
        
        // Xử lý range parameter nếu không có startDate/endDate
        if (range != null && startDate == null && endDate == null) {
            Calendar calendar = Calendar.getInstance();
            endDate = calendar.getTime();
            
            switch (range) {
                case "7d":
                    calendar.add(Calendar.DAY_OF_MONTH, -7);
                    break;
                case "30d":
                    calendar.add(Calendar.DAY_OF_MONTH, -30);
                    break;
                case "90d":
                    calendar.add(Calendar.DAY_OF_MONTH, -90);
                    break;
                case "6m":
                    calendar.add(Calendar.MONTH, -6);
                    break;
                case "1y":
                    calendar.add(Calendar.YEAR, -1);
                    break;
                default:
                    calendar.add(Calendar.MONTH, -6); // Default to 6 months
                    break;
            }
            startDate = calendar.getTime();
        }
        
        // Set endDate to end of day (23:59:59)
        if (endDate != null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(endDate);
            calendar.set(Calendar.HOUR_OF_DAY, 23);
            calendar.set(Calendar.MINUTE, 59);
            calendar.set(Calendar.SECOND, 59);
            endDate = calendar.getTime();
        }

        // Không dùng sorting từ Pageable vì revenue được tính toán trong query
        // Sorting sẽ được handle trong query của repository
        Pageable pageable = PageRequest.of(page, size);
        Page<CategoryAnalyticsDetailResponseDTO> categoryAnalyticsPage =
                analyticsService.getCategoryAnalyticsDetails(startDate, endDate, pageable);

        ResponseGeneral<Page<CategoryAnalyticsDetailResponseDTO>> response = new ResponseGeneral<>();
        response.setData(categoryAnalyticsPage);
        response.setMessage(SUCCESS);
        response.setDetail("Category analytics details retrieved successfully");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/courses/details")
    public ResponseEntity<ResponseGeneral<Page<CourseAnalyticsDetailResponseDTO>>> getCourseAnalyticsDetails(
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate,
            @RequestParam(required = false) String range,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "revenue,desc") String[] sort
    ) throws AnalyticsRetrievalException {
        log.info("Fetching course analytics details - page: {}, size: {}, sort: {}", page, size, sort);
        
        // Xử lý range parameter nếu không có startDate/endDate
        if (range != null && startDate == null && endDate == null) {
            Calendar calendar = Calendar.getInstance();
            endDate = calendar.getTime();
            
            switch (range) {
                case "7d":
                    calendar.add(Calendar.DAY_OF_MONTH, -7);
                    break;
                case "30d":
                    calendar.add(Calendar.DAY_OF_MONTH, -30);
                    break;
                case "90d":
                    calendar.add(Calendar.DAY_OF_MONTH, -90);
                    break;
                case "6m":
                    calendar.add(Calendar.MONTH, -6);
                    break;
                case "1y":
                    calendar.add(Calendar.YEAR, -1);
                    break;
                default:
                    calendar.add(Calendar.MONTH, -6); // Default to 6 months
                    break;
            }
            startDate = calendar.getTime();
        }
        
        // Set endDate to end of day (23:59:59)
        if (endDate != null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(endDate);
            calendar.set(Calendar.HOUR_OF_DAY, 23);
            calendar.set(Calendar.MINUTE, 59);
            calendar.set(Calendar.SECOND, 59);
            endDate = calendar.getTime();
        }

        // Sort được handle hoàn toàn trong repository query, không dùng Pageable sorting
        Pageable pageable = PageRequest.of(page, size);
        
        Page<CourseAnalyticsDetailResponseDTO> courseAnalyticsPage =
                analyticsService.getCourseAnalyticsDetails(startDate, endDate, pageable);

        ResponseGeneral<Page<CourseAnalyticsDetailResponseDTO>> response = new ResponseGeneral<>();
        response.setData(courseAnalyticsPage);
        response.setMessage(SUCCESS);
        response.setDetail("Course analytics details retrieved successfully");
        
        log.info("Successfully returned {} course analytics records", courseAnalyticsPage.getContent().size());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

} 