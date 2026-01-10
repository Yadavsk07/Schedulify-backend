package com.example.schedulify.controller;

import com.example.schedulify.dto.CreateClassGroupDTO;
import com.example.schedulify.model.ClassGroup;
import com.example.schedulify.model.Section;
import com.example.schedulify.repository.ClassGroupRepository;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/classes")
@CrossOrigin
public class ClassGroupController {

    private final ClassGroupRepository classRepo;

    public ClassGroupController(ClassGroupRepository classRepo) {
        this.classRepo = classRepo;
    }

    private void validateAdmin(HttpServletRequest req, String schoolId) {
        Claims c = (Claims) req.getAttribute("jwtClaims");
        if (c == null || !"ADMIN".equals(c.get("role")))
            throw new RuntimeException("Unauthorized");
        if (!schoolId.equals(c.get("schoolId")))
            throw new RuntimeException("School mismatch");
    }

    @PostMapping("/{schoolId}")
    public ResponseEntity<?> createClassGroup(@PathVariable String schoolId,
                                              @RequestBody CreateClassGroupDTO dto,
                                              HttpServletRequest req) {
        validateAdmin(req, schoolId);

        ClassGroup cg = new ClassGroup();
        cg.setId(UUID.randomUUID().toString());
        cg.setSchoolId(schoolId);
        cg.setName(dto.getName());

        // ✅ Directly set sections as strings
        cg.setSections(dto.getSectionIds());

        cg.setSubjectIds(dto.getSubjectIds());

        classRepo.save(cg);
        return ResponseEntity.ok(cg);
    }

    @GetMapping("/{schoolId}")
    public List<ClassGroup> getClassGroups(@PathVariable String schoolId, HttpServletRequest req) {
        validateAdmin(req, schoolId);
        return classRepo.findBySchoolId(schoolId);
    }

    @GetMapping("/{schoolId}/{classId}")
    public ResponseEntity<?> getClassGroup(@PathVariable String schoolId,
                                           @PathVariable String classId,
                                           HttpServletRequest req) {
        validateAdmin(req, schoolId);

        Optional<ClassGroup> opt = classRepo.findById(classId);
        if (opt.isEmpty() || !opt.get().getSchoolId().equals(schoolId))
            return ResponseEntity.notFound().build();

        return ResponseEntity.ok(opt.get());
    }

    @PutMapping("/{schoolId}/{classId}")
    public ResponseEntity<?> updateClassGroup(@PathVariable String schoolId,
                                              @PathVariable String classId,
                                              @RequestBody Map<String,Object> body,
                                              HttpServletRequest req) {
        validateAdmin(req, schoolId);

        Optional<ClassGroup> opt = classRepo.findById(classId);
        if (opt.isEmpty() || !opt.get().getSchoolId().equals(schoolId))
            return ResponseEntity.notFound().build();

        ClassGroup cg = opt.get();

        if (body.containsKey("name")) cg.setName(body.get("name").toString());

        if (body.containsKey("sectionIds")) {
            // ✅ Cast directly to List<String>
            cg.setSections((List<String>) body.get("sectionIds"));
        }

        if (body.containsKey("subjectIds")) {
            cg.setSubjectIds((List<String>) body.get("subjectIds"));
        }

        classRepo.save(cg);
        return ResponseEntity.ok(cg);
    }

    @DeleteMapping("/{schoolId}/{classId}")
    public ResponseEntity<?> deleteClassGroup(@PathVariable String schoolId,
                                              @PathVariable String classId,
                                              HttpServletRequest req) {
        validateAdmin(req, schoolId);

        Optional<ClassGroup> opt = classRepo.findById(classId);
        if (opt.isEmpty() || !opt.get().getSchoolId().equals(schoolId))
            return ResponseEntity.notFound().build();

        classRepo.delete(opt.get());
        return ResponseEntity.ok(Map.of("deleted", true));
    }
}
