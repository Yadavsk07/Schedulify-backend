package com.example.schedulify.dto;

import lombok.Data;

@Data
public class SlotDto {
    private String id;
    private String day;
    private int period;
    private String subjectId;
    private String subjectName;
    private String teacherId;
    private String teacherName;
    private String labRoomId;
    private String labRoomName;
    private boolean locked;
    private String classGroupId;
    private String sectionId;
}
