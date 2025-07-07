package com.coursehub.dto.request.announcement;

import com.coursehub.enums.TargetGroup;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AnnouncementCreateRequestDTO {
    private String title;
    private String content;
    private TargetGroup targetGroup;
    private String link;

}
