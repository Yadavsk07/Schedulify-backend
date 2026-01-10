package com.example.schedulify.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document("school_settings")
@Data
public class SchoolSettings {
    @Id
    private String id;
    private String schoolId;
    private List<String> workingDays; // e.g., MON,TUE,...
    private int periodsPerDay = 8;
    private int totalDaysPerWeek = 5;
    private boolean hasMorningAssembly = false;
    private int assemblySlot = 0; // 0-based period
    private boolean saturdayHalfDay = false;
}
