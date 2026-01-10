package com.example.schedulify.dto;

import lombok.Data;

@Data
public class CreateClassSubjectDTO {

    private String classId;
    private String subjectId;
    private String teacherId;

    private int periodsPerWeek;

    private String roomType; // CLASSROOM / LAB

    private boolean requiresConsecutive;
    private int consecutiveSize;
}
