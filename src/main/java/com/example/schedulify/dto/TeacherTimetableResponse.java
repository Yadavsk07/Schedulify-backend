package com.example.schedulify.dto;

import lombok.Data;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Data
public class TeacherTimetableResponse {
    private String teacherId;
    private Map<String, List<SlotDto>> timetable = new LinkedHashMap<>();
}
