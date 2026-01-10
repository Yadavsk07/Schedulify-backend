package com.example.schedulify.controller;

import com.example.schedulify.model.Subject;
import com.example.schedulify.model.Teacher;
import com.example.schedulify.repository.SubjectRepository;
import com.example.schedulify.repository.TeacherRepository;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/meta")
@CrossOrigin
public class MetaController {

    private final TeacherRepository teacherRepo;
    private final SubjectRepository subjectRepo;

    public MetaController(TeacherRepository teacherRepo, SubjectRepository subjectRepo) {
        this.teacherRepo = teacherRepo;
        this.subjectRepo = subjectRepo;
    }

    // ---------------------------------------------------------
    // GET SUBJECT + TEACHER NAME MAPS
    // ---------------------------------------------------------
    @GetMapping("/{schoolId}")
    public Map<String, Object> getMetadata(@PathVariable String schoolId) {

        Map<String, String> subjectMap = new HashMap<>();
        for (Subject s : subjectRepo.findBySchoolId(schoolId)) {
            subjectMap.put(s.getId(), s.getName());
        }

        Map<String, String> teacherMap = new HashMap<>();
        for (Teacher t : teacherRepo.findBySchoolId(schoolId)) {
            teacherMap.put(t.getId(), t.getName());
        }

        Map<String, Object> result = new HashMap<>();
        result.put("subjects", subjectMap);
        result.put("teachers", teacherMap);

        return result;
    }
}
