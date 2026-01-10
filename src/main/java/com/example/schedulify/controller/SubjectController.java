package com.example.schedulify.controller;

import com.example.schedulify.dto.CreateSubjectDTO;
import com.example.schedulify.model.RoomType;
import com.example.schedulify.model.Subject;
import com.example.schedulify.repository.SubjectRepository;
import jakarta.servlet.http.HttpServletRequest;
import io.jsonwebtoken.Claims;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/subjects")
@CrossOrigin
public class SubjectController {

    private final SubjectRepository subjectRepo;

    public SubjectController(SubjectRepository subjectRepo) {
        this.subjectRepo = subjectRepo;
    }

    private void validateAdmin(HttpServletRequest req, String schoolId) {
        Claims c = (Claims) req.getAttribute("jwtClaims");
        if (c == null || !"ADMIN".equals(c.get("role")))
            throw new RuntimeException("Unauthorized");
        if (!schoolId.equals(c.get("schoolId")))
            throw new RuntimeException("School mismatch");
    }

    // ---------------- CREATE ----------------
    @PostMapping("/{schoolId}")
    public ResponseEntity<?> createSubject(@PathVariable String schoolId,
                                           @RequestBody CreateSubjectDTO dto,
                                           HttpServletRequest req) {
        validateAdmin(req, schoolId);

        Subject s = new Subject();
        s.setId(dto.getId()); // manual or auto handled at service/db level
        s.setSchoolId(schoolId);
        s.setName(dto.getName());
        s.setCode(dto.getCode());
        s.setRequiresConsecutive(dto.isRequiresConsecutive());
        s.setConsecutiveSize(dto.getConsecutiveSize());

        try {
            s.setRoomType(RoomType.valueOf(dto.getRoomType().toUpperCase()));
        } catch (Exception ex) {
            s.setRoomType(RoomType.CLASSROOM);
        }

        subjectRepo.save(s);
        return ResponseEntity.ok(s);
    }

    // ---------------- READ ----------------
    @GetMapping("/{schoolId}")
    public List<Subject> getSubjects(@PathVariable String schoolId, HttpServletRequest req) {
        validateAdmin(req, schoolId);
        return subjectRepo.findBySchoolId(schoolId);
    }

    @GetMapping("/{schoolId}/{subjectId}")
    public ResponseEntity<?> getSubject(@PathVariable String schoolId,
                                        @PathVariable String subjectId,
                                        HttpServletRequest req) {
        validateAdmin(req, schoolId);

        Optional<Subject> s = subjectRepo.findById(subjectId);
        if (s.isEmpty() || !s.get().getSchoolId().equals(schoolId))
            return ResponseEntity.notFound().build();

        return ResponseEntity.ok(s.get());
    }

    // ---------------- UPDATE ----------------
    @PutMapping("/{schoolId}/{subjectId}")
    public ResponseEntity<?> updateSubject(@PathVariable String schoolId,
                                           @PathVariable String subjectId,
                                           @RequestBody Map<String, Object> body,
                                           HttpServletRequest req) {
        validateAdmin(req, schoolId);

        Optional<Subject> opt = subjectRepo.findById(subjectId);
        if (opt.isEmpty() || !opt.get().getSchoolId().equals(schoolId))
            return ResponseEntity.notFound().build();

        Subject s = opt.get();

        if (body.containsKey("name"))
            s.setName(body.get("name").toString());

        if (body.containsKey("code"))
            s.setCode(body.get("code").toString());

        if (body.containsKey("requiresConsecutive"))
            s.setRequiresConsecutive(Boolean.parseBoolean(body.get("requiresConsecutive").toString()));

        if (body.containsKey("consecutiveSize"))
            s.setConsecutiveSize(Integer.parseInt(body.get("consecutiveSize").toString()));

        if (body.containsKey("roomType")) {
            try {
                s.setRoomType(RoomType.valueOf(body.get("roomType").toString().toUpperCase()));
            } catch (Exception ex) {
                s.setRoomType(RoomType.CLASSROOM);
            }
        }

        subjectRepo.save(s);
        return ResponseEntity.ok(s);
    }

    // ---------------- DELETE ----------------
    @DeleteMapping("/{schoolId}/{subjectId}")
    public ResponseEntity<?> deleteSubject(@PathVariable String schoolId,
                                           @PathVariable String subjectId,
                                           HttpServletRequest req) {
        validateAdmin(req, schoolId);

        Optional<Subject> opt = subjectRepo.findById(subjectId);
        if (opt.isEmpty() || !opt.get().getSchoolId().equals(schoolId))
            return ResponseEntity.notFound().build();

        subjectRepo.delete(opt.get());
        return ResponseEntity.ok(Map.of("deleted", true));
    }
}
