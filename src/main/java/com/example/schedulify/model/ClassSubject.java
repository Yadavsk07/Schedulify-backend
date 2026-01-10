package com.example.schedulify.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("class_subjects")
@Data
public class ClassSubject {

    @Id
    private String id;

    private String schoolId;

    private String classGroupId;     // C6, C7, C10 etc
    private String subjectId;   // S01, S15 etc
    private String teacherId;   // T01 etc

    private int periodsPerWeek; // PER CLASS PER WEEK

    private RoomType roomType = RoomType.CLASSROOM;

    private boolean requiresConsecutive = false;
    private int consecutiveSize = 0;
}
