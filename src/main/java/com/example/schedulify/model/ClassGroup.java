package com.example.schedulify.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document("class_groups")
@Data
public class ClassGroup {
    @Id
    private String id;

    private String schoolId;
    private String name; // e.g., "Class 8"
    private List<String> sections;
    private List<String> subjectIds;
}
