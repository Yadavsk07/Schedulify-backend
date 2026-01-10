package com.example.schedulify.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.Map;

@Document("teachers")
@Data
public class Teacher {

    @Id
    private String id;

    private String schoolId;
    private String teacherCode;
    private String name;

    private List<String> subjectIds;
    private List<String> classGroupIds;

    // MongoDB stores enums as STRING automatically
    private ClassLevel level;

    private int maxPeriodsPerWeek;
    private Map<String, List<Integer>> unavailable;
    private List<Integer> preferredOffPeriods;

    public void setClassIds(List<String> csv) {
        this.classGroupIds = csv;
    }
}
