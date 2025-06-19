package com.coursehub.dto.request.user;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WarnRequestDTO {
    private String resourceType;
    private Long resourceId;
}
