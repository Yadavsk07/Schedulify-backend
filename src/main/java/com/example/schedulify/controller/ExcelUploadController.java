package com.example.schedulify.controller;

import com.example.schedulify.service.ExcelService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/upload")
@CrossOrigin
public class ExcelUploadController {

    private final ExcelService excelService;

    public ExcelUploadController(ExcelService excelService) {
        this.excelService = excelService;
    }

    // POST /api/upload/{schoolId}/master : master excel with sheets
    @PostMapping("/{schoolId}/master")
    public ResponseEntity<?> uploadMaster(@PathVariable String schoolId,
                                          @RequestParam("file") MultipartFile file,
                                          HttpServletRequest req) {
        try {
            // Call the updated ExcelService which now imports ClassSubjects
            Map<String, Integer> result = excelService.importMasterExcel(file, schoolId);

            // Return a clean response including classSubjects count
            return ResponseEntity.ok(Map.of(
                    "teachersImported", result.getOrDefault("teachers", 0),
                    "subjectsImported", result.getOrDefault("subjects", 0),
                    "classesImported", result.getOrDefault("classes", 0),
                    "labsImported", result.getOrDefault("labs", 0),
                    "classSubjectsImported", result.getOrDefault("classSubjects", 0)
            ));
        } catch (Exception ex) {
            ex.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error", ex.getMessage()));
        }
    }
}
