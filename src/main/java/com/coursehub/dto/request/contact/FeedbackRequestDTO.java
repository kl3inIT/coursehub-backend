package com.coursehub.dto.request.contact;

import com.coursehub.enums.FeedbackType;
import lombok.Data;

@Data
public class FeedbackRequestDTO {

    private String fullName;
    private String email;
    private FeedbackType category;
    private String subject;
    private String message;

}
