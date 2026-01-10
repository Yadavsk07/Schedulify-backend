package com.example.schedulify.controller;

import com.example.schedulify.dto.ClassTimetableResponse;
import com.example.schedulify.dto.TeacherTimetableResponse;
import com.example.schedulify.model.ClassGroup;
import com.example.schedulify.model.Section;
import com.example.schedulify.repository.ClassGroupRepository;
import com.example.schedulify.service.TimetableService;
import com.example.schedulify.service.TimetableQueryService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/timetable")
@CrossOrigin
public class TimetableController {

    private final TimetableService timetableService;
    private final TimetableQueryService queryService;

    public TimetableController(TimetableService timetableService,
                               TimetableQueryService queryService) {
        this.timetableService = timetableService;
        this.queryService = queryService;
    }

    @PostMapping("/generate/school/{schoolId}")
    public ResponseEntity<?> generateSchool(@PathVariable String schoolId) {
        boolean ok = timetableService.generateFullSchoolTimetable(schoolId);
        return ResponseEntity.ok(Map.of(
                "status", ok ? "ok" : "failed"
        ));
    }

    @GetMapping("/class/{schoolId}/{classId}/{sectionId}")
    public ClassTimetableResponse getClassTimetable(
            @PathVariable String schoolId,
            @PathVariable String classId,
            @PathVariable String sectionId) {
        return queryService.getClassTimetable(schoolId, classId, sectionId);
    }

    @GetMapping("/teacher/{schoolId}/{teacherId}")
    public TeacherTimetableResponse getTeacherTimetable(
            @PathVariable String schoolId,
            @PathVariable String teacherId) {
        return queryService.getTeacherTimetable(schoolId, teacherId);
    }
}
