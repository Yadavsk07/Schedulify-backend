package com.example.schedulify.model;

import lombok.Data;
import com.example.schedulify.model.RoomType;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


@Document("subjects")
@Data
public class Subject {

    @Id
    private String id;

    private String schoolId;
    private String name;
    private String code;
    //private int periodsPerWeek;
    private boolean requiresConsecutive;
    private int consecutiveSize;
    private RoomType roomType = RoomType.CLASSROOM;
    private String teacherId; // optional: preferred teacher
}
