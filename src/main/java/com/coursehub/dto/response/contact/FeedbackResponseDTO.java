package com.coursehub.dto.response.contact;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.util.Date;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FeedbackResponseDTO {

    private Long id;
    private String fullName;
    private String email;
    private String subject;
    private String message;
    private String category;
    private String adminReply;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Ho_Chi_Minh")
    private Date createdAt;
}
