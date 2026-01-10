package com.example.schedulify.controller;


import com.example.schedulify.model.School;
import com.example.schedulify.service.SchoolService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/schools")
@CrossOrigin
public class SchoolController {

    private final SchoolService schoolService;

    public SchoolController(SchoolService schoolService) {
        this.schoolService = schoolService;
    }

    @GetMapping("/{schoolId}")
    public ResponseEntity<?> getSchool(@PathVariable String schoolId) {
        School school = schoolService.getSchoolById(schoolId);
        if (school == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(school);
    }
}