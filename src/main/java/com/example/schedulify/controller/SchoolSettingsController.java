package com.example.schedulify.controller;

import com.example.schedulify.model.School;
import com.example.schedulify.model.SchoolSettings;
import com.example.schedulify.service.SchoolSettingsService;
import com.example.schedulify.service.SchoolService;   // ✅ use the proper SchoolService
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/settings")
@CrossOrigin
public class SchoolSettingsController {

    private final SchoolSettingsService settingsService;
    private final SchoolService schoolService; // ✅ correct type

    public SchoolSettingsController(SchoolSettingsService settingsService,
                                    SchoolService schoolService) {
        this.settingsService = settingsService;
        this.schoolService = schoolService;
    }

    private void validateAdmin(HttpServletRequest req, String schoolId) {
        Claims claims = (Claims) req.getAttribute("jwtClaims");
        if (claims == null) throw new RuntimeException("Unauthorized");

        if (!"ADMIN".equals(claims.get("role"))) {
            throw new RuntimeException("Forbidden - Admin only");
        }

        String sid = (String) claims.get("schoolId");
        if (!sid.equals(schoolId)) {
            throw new RuntimeException("School mismatch");
        }
    }

    // ---------------------------
    // GET Settings + School Info
    // ---------------------------
    @GetMapping("/{schoolId}")
    public ResponseEntity<?> getSettings(@PathVariable String schoolId,
                                         HttpServletRequest req) {
        validateAdmin(req, schoolId);

        SchoolSettings settings = settingsService.getSettingsBySchool(schoolId);
        School school = schoolService.getSchoolById(schoolId);

        Map<String, Object> response = new HashMap<>();
        response.put("schoolName", school != null ? school.getName() : "");
        response.put("email", school != null ? school.getAdminEmail() : "");
        response.put("settings", settings);

        return ResponseEntity.ok(response);
    }

    // ---------------------------
    // UPDATE Settings
    // ---------------------------
    @PutMapping("/{schoolId}")
    public ResponseEntity<?> updateSettings(@PathVariable String schoolId,
                                            @RequestBody SchoolSettings newSettings,
                                            HttpServletRequest req) {
        validateAdmin(req, schoolId);

        newSettings.setSchoolId(schoolId);
        SchoolSettings updated = settingsService.updateSettings(schoolId, newSettings);
        return ResponseEntity.ok(updated);
    }

    // ---------------------------
    // RESET to default
    // ---------------------------
    @PostMapping("/{schoolId}/reset")
    public ResponseEntity<?> resetToDefault(@PathVariable String schoolId,
                                            HttpServletRequest req) {
        validateAdmin(req, schoolId);

        SchoolSettings defaults = settingsService.resetToDefault(schoolId);
        return ResponseEntity.ok(defaults);
    }
}