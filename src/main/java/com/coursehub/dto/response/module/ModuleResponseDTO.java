package com.coursehub.dto.response.module;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ModuleResponseDTO {
    private Long id;
    private String title;
    private Long orderNumber;
    private Long courseId;

}
