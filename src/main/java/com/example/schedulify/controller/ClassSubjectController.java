package com.example.schedulify.controller;

import com.example.schedulify.dto.CreateClassSubjectDTO;
import com.example.schedulify.model.ClassSubject;
import com.example.schedulify.model.RoomType;
import com.example.schedulify.repository.ClassSubjectRepository;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/class-subjects")
@CrossOrigin
public class ClassSubjectController {

    private final ClassSubjectRepository repo;

    public ClassSubjectController(ClassSubjectRepository repo) {
        this.repo = repo;
    }

    // ================= AUTH =================
    private void validateAdmin(HttpServletRequest req, String schoolId) {
        Claims c = (Claims) req.getAttribute("jwtClaims");
        if (c == null || !"ADMIN".equals(c.get("role")))
            throw new RuntimeException("Unauthorized");
        if (!schoolId.equals(c.get("schoolId")))
            throw new RuntimeException("School mismatch");
    }

    // ================= CREATE =================
    @PostMapping("/{schoolId}")
    public ResponseEntity<?> create(
            @PathVariable String schoolId,
            @RequestBody CreateClassSubjectDTO dto,
            HttpServletRequest req) {

        validateAdmin(req, schoolId);

        // Prevent duplicate subject for same class
        if (repo.existsBySchoolIdAndClassGroupIdAndSubjectId(
                schoolId, dto.getClassId(), dto.getSubjectId())) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Subject already assigned to this class"));
        }

        ClassSubject cs = new ClassSubject();
        cs.setSchoolId(schoolId);
        cs.setClassGroupId(dto.getClassId());
        cs.setSubjectId(dto.getSubjectId());
        cs.setTeacherId(dto.getTeacherId());
        cs.setPeriodsPerWeek(dto.getPeriodsPerWeek());
        cs.setRequiresConsecutive(dto.isRequiresConsecutive());
        cs.setConsecutiveSize(
                dto.isRequiresConsecutive() ? dto.getConsecutiveSize() : 0
        );

        try {
            cs.setRoomType(RoomType.valueOf(dto.getRoomType().toUpperCase()));
        } catch (Exception e) {
            cs.setRoomType(RoomType.CLASSROOM);
        }

        repo.save(cs);
        return ResponseEntity.ok(cs);
    }

    // ================= READ ALL =================
    @GetMapping("/{schoolId}")
    public List<ClassSubject> getAll(
            @PathVariable String schoolId,
            HttpServletRequest req) {

        validateAdmin(req, schoolId);
        return repo.findBySchoolId(schoolId);
    }

    // ================= READ BY CLASS =================
    @GetMapping("/{schoolId}/class/{classId}")
    public List<ClassSubject> getByClass(
            @PathVariable String schoolId,
            @PathVariable String classId,
            HttpServletRequest req) {

        validateAdmin(req, schoolId);
        return repo.findBySchoolIdAndClassGroupId(schoolId, classId);
    }

    // ================= DELETE =================
    @DeleteMapping("/{schoolId}/{id}")
    public ResponseEntity<?> delete(
            @PathVariable String schoolId,
            @PathVariable String id,
            HttpServletRequest req) {

        validateAdmin(req, schoolId);
        repo.deleteById(id);
        return ResponseEntity.ok(Map.of("deleted", true));
    }
}
