package com.example.schedulify.controller;

import com.example.schedulify.dto.CreateTeacherDTO;
import com.example.schedulify.model.Teacher;
import com.example.schedulify.repository.TeacherRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.jsonwebtoken.Claims;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/teachers")
@CrossOrigin
public class TeacherController {

    private final TeacherRepository teacherRepo;

    public TeacherController(TeacherRepository teacherRepo) {
        this.teacherRepo = teacherRepo;
    }

    private String validateAdmin(HttpServletRequest req, String pathSchoolId) {
        Claims claims = (Claims) req.getAttribute("jwtClaims");
        if (claims == null) throw new RuntimeException("Unauthorized");
        if (!"ADMIN".equals(claims.get("role"))) throw new RuntimeException("Forbidden");
        String sid = (String) claims.get("schoolId");
        if (!sid.equals(pathSchoolId)) throw new RuntimeException("School mismatch");
        return sid;
    }

    // ---------------- CREATE ----------------
    @PostMapping("/{schoolId}")
    public ResponseEntity<?> createTeacher(@PathVariable String schoolId,
                                           @RequestBody CreateTeacherDTO dto,
                                           HttpServletRequest req) {
        validateAdmin(req, schoolId);

        Teacher t = new Teacher();
        // ✅ Use provided ID if available, otherwise generate UUID
        if (dto.getId() != null && !dto.getId().isBlank()) {
            t.setId(dto.getId());
        } else {
            t.setId(UUID.randomUUID().toString());
        }

        t.setSchoolId(schoolId);
        t.setTeacherCode(dto.getTeacherCode());
        t.setName(dto.getName());
        t.setSubjectIds(dto.getSubjectIds());
        t.setClassGroupIds(dto.getClassGroupIds());
        t.setMaxPeriodsPerWeek(dto.getMaxPeriodsPerWeek());
        t.setUnavailable(dto.getUnavailable());

        teacherRepo.save(t);
        return ResponseEntity.ok(t);
    }

    // ---------------- READ ----------------
    @GetMapping("/{schoolId}")
    public List<Teacher> getTeachers(@PathVariable String schoolId, HttpServletRequest req) {
        validateAdmin(req, schoolId);
        return teacherRepo.findBySchoolId(schoolId);
    }

    @GetMapping("/{schoolId}/{teacherId}")
    public ResponseEntity<?> getTeacher(@PathVariable String schoolId,
                                        @PathVariable String teacherId,
                                        HttpServletRequest req) {
        validateAdmin(req, schoolId);
        Optional<Teacher> t = teacherRepo.findById(teacherId);
        if (t.isEmpty() || !t.get().getSchoolId().equals(schoolId))
            return ResponseEntity.notFound().build();
        return ResponseEntity.ok(t.get());
    }

    // ---------------- UPDATE ----------------
    @PutMapping("/{schoolId}/{teacherId}")
    public ResponseEntity<?> updateTeacher(@PathVariable String schoolId,
                                           @PathVariable String teacherId,
                                           @RequestBody Map<String, Object> body,
                                           HttpServletRequest req) {
        validateAdmin(req, schoolId);

        Optional<Teacher> opt = teacherRepo.findById(teacherId);
        if (opt.isEmpty() || !opt.get().getSchoolId().equals(schoolId))
            return ResponseEntity.notFound().build();

        Teacher t = opt.get();

        if (body.containsKey("name"))
            t.setName(body.get("name").toString());
        if (body.containsKey("teacherCode"))
            t.setTeacherCode(body.get("teacherCode").toString());
        if (body.containsKey("subjectIds"))
            t.setSubjectIds((List<String>) body.get("subjectIds"));
        if (body.containsKey("classGroupIds"))
            t.setClassGroupIds((List<String>) body.get("classGroupIds"));
        if (body.containsKey("maxPeriodsPerWeek"))
            t.setMaxPeriodsPerWeek(Integer.parseInt(body.get("maxPeriodsPerWeek").toString()));
        if (body.containsKey("unavailable"))
            t.setUnavailable((Map<String, List<Integer>>) body.get("unavailable"));

        teacherRepo.save(t);
        return ResponseEntity.ok(t);
    }

    // ---------------- DELETE ----------------
    @DeleteMapping("/{schoolId}/{teacherId}")
    public ResponseEntity<?> deleteTeacher(@PathVariable String schoolId,
                                           @PathVariable String teacherId,
                                           HttpServletRequest req) {
        validateAdmin(req, schoolId);

        Optional<Teacher> opt = teacherRepo.findById(teacherId);
        if (opt.isEmpty() || !opt.get().getSchoolId().equals(schoolId))
            return ResponseEntity.notFound().build();

        teacherRepo.delete(opt.get());
        return ResponseEntity.ok(Map.of("deleted", true));
    }
}