package com.coursehub.dto.request.announcement;

import com.coursehub.enums.AnnouncementStatus;
import com.coursehub.enums.AnnouncementType;
import com.coursehub.enums.TargetGroup;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AnnouncementSearchRequest {
    private AnnouncementType type;
    private TargetGroup targetGroup;
    private String search;
    private Integer page = 0;
    private Integer size = 10;
    private String sortBy = "createdDate";
    private String direction = "DESC";
    private String mode;
    private AnnouncementStatus status;
    private Long isDeleted; // Đổi sang Long
} 