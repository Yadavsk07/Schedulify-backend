package com.example.schedulify.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("timetable_slots")
@Data
public class TimetableSlot {
    @Id
    private String id;

    private String schoolId;
    private String classGroupId;
    private String sectionId;
    private String day;   // MON..SAT
    private int period;   // 0-based index

    private String subjectId;
    private String teacherId;
    private String labRoomId;

    private boolean locked = false;
}
