package com.example.schedulify.dto;

import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
public class CreateTeacherDTO {
    private String Id;
    private String teacherCode;
    private String name;
    private List<String> subjectIds;
    private List<String> classGroupIds;
    private int maxPeriodsPerWeek;
    private Map<String, List<Integer>> unavailable;


}
