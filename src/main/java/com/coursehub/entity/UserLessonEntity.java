package com.coursehub.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "user_lesson")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserLessonEntity extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lesson_id")
    private LessonEntity lessonEntity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserEntity userEntity;

    @Column(name = "video_current_time")
    @Builder.Default
    private Long currentTime = 0L; // Current position in seconds

    @Column(name = "watched_time")
    @Builder.Default
    private Long watchedTime = 0L; // Current position in seconds

    @Column(name = "is_completed")
    @Builder.Default
    private Long isCompleted = 0L; // 0 for false, 1 for true

}
