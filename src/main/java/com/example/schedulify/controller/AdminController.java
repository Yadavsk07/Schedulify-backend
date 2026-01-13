package com.example.schedulify.controller;

import com.example.schedulify.repository.*;

import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
//@CrossOrigin(origins = "https://schedulify01.netlify.app/")
public class AdminController {

    private final TeacherRepository teacherRepository;
    private final SubjectRepository subjectRepository;
    private final ClassGroupRepository classGroupRepository;
    private final LabRoomRepository labRoomRepository;
    private final ClassSubjectRepository classSubjectRepository;

    public AdminController(TeacherRepository teacherRepository,
                           SubjectRepository subjectRepository,
                           ClassGroupRepository classGroupRepository,
                           LabRoomRepository labRoomRepository,
                           ClassSubjectRepository classSubjectRepository) {
        this.teacherRepository = teacherRepository;
        this.subjectRepository = subjectRepository;
        this.classGroupRepository = classGroupRepository;
        this.labRoomRepository = labRoomRepository;
        this.classSubjectRepository = classSubjectRepository;
    }

    // ----------------------------------------
    // ADMIN DASHBOARD – SCHOOL STATISTICS
    // ----------------------------------------
    @GetMapping("/stats/{schoolId}")
    public Map<String, Object> getSchoolStats(@PathVariable String schoolId) {

        long teachers = teacherRepository.countBySchoolId(schoolId);
        long subjects = subjectRepository.countBySchoolId(schoolId);
        long classes = classGroupRepository.countBySchoolId(schoolId);
        long labs = labRoomRepository.countBySchoolId(schoolId);
        long classSubjects = classSubjectRepository.countBySchoolId(schoolId);

        Map<String, Object> result = new HashMap<>();
        result.put("teachers", teachers);
        result.put("subjects", subjects);
        result.put("classes", classes);
        result.put("labs", labs);
        result.put("classSubjects", classSubjects);

        return result;
    }
}
