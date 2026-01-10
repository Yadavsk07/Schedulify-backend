package com.example.schedulify.controller;

import com.example.schedulify.dto.CreateLabRoomDTO;
import com.example.schedulify.model.LabRoom;
import com.example.schedulify.repository.LabRoomRepository;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/labs")
@CrossOrigin
public class LabRoomController {

    private final LabRoomRepository labRepo;

    public LabRoomController(LabRoomRepository labRepo) {
        this.labRepo = labRepo;
    }

    private void validateAdmin(HttpServletRequest req, String schoolId) {
        Claims c = (Claims) req.getAttribute("jwtClaims");
        if (c == null || !"ADMIN".equals(c.get("role")))
            throw new RuntimeException("Unauthorized");
        if (!schoolId.equals(c.get("schoolId")))
            throw new RuntimeException("School mismatch");
    }

    @PostMapping("/{schoolId}")
    public ResponseEntity<?> createLab(@PathVariable String schoolId,
                                       @RequestBody CreateLabRoomDTO dto,
                                       HttpServletRequest req) {
        validateAdmin(req, schoolId);

        if (dto.getId() == null || dto.getId().isBlank()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Lab ID is required"));
        }

        // Enforce uniqueness (VERY IMPORTANT)
        if (labRepo.existsById(dto.getId())) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Lab ID already exists"));
        }

        LabRoom lr = new LabRoom();
        lr.setId(dto.getId().trim());   // ✅ KEEP USER ID
        lr.setSchoolId(schoolId);
        lr.setName(dto.getName());
        lr.setSubjectType(dto.getSubjectType());
        lr.setCapacity(dto.getCapacity());

        labRepo.save(lr);
        return ResponseEntity.ok(lr);
    }


    @GetMapping("/{schoolId}")
    public List<LabRoom> getLabs(@PathVariable String schoolId, HttpServletRequest req) {
        validateAdmin(req, schoolId);
        return labRepo.findBySchoolId(schoolId);
    }

    @GetMapping("/{schoolId}/{labId}")
    public ResponseEntity<?> getLab(@PathVariable String schoolId,
                                    @PathVariable String labId,
                                    HttpServletRequest req) {
        validateAdmin(req, schoolId);

        Optional<LabRoom> opt = labRepo.findById(labId);
        if (opt.isEmpty() || !opt.get().getSchoolId().equals(schoolId))
            return ResponseEntity.notFound().build();

        return ResponseEntity.ok(opt.get());
    }

    @PutMapping("/{schoolId}/{labId}")
    public ResponseEntity<?> updateLab(@PathVariable String schoolId,
                                       @PathVariable String labId,
                                       @RequestBody Map<String,Object> body,
                                       HttpServletRequest req) {
        validateAdmin(req, schoolId);

        Optional<LabRoom> opt = labRepo.findById(labId);
        if (opt.isEmpty() || !opt.get().getSchoolId().equals(schoolId))
            return ResponseEntity.notFound().build();

        LabRoom lr = opt.get();

        if (body.containsKey("name")) lr.setName(body.get("name").toString());
        if (body.containsKey("subjectType")) lr.setSubjectType(body.get("subjectType").toString());
        if (body.containsKey("capacity")) lr.setCapacity((int) body.get("capacity"));

        labRepo.save(lr);
        return ResponseEntity.ok(lr);
    }

    @DeleteMapping("/{schoolId}/{labId}")
    public ResponseEntity<?> deleteLab(@PathVariable String schoolId,
                                       @PathVariable String labId,
                                       HttpServletRequest req) {
        validateAdmin(req, schoolId);

        Optional<LabRoom> opt = labRepo.findById(labId);
        if (opt.isEmpty() || !opt.get().getSchoolId().equals(schoolId))
            return ResponseEntity.notFound().build();

        labRepo.delete(opt.get());
        return ResponseEntity.ok(Map.of("deleted", true));
    }
}
