package com.example.schedulify.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("lab_rooms")
@Data
public class LabRoom {
    @Id
    private String id;

    private String schoolId;
    private String name;
    private String subjectType; // e.g., "Science", "Computer"
    private int capacity;
}
